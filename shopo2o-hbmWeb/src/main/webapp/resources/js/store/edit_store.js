/**
 * Created by admin on 2017-08-22.
 */
var map, sendAreaMap, district, polygons = [];
var mouseTool; //画多边形
var editor = {};
var storeMarker; //sendAreaMap 中门店点标记
var regionPolygon; //配送范围多边形
var regionMarkers = []; //划分配配送范围点
var regionMarkerIndex = 2; //划分配送范围点序号
var regionMarkerPosition = {}; //划分配送范围点集合
var regionRegionDivision = {}; //配送区域集合
var contextMenu = {}; //右键菜单
var citySelect = document.getElementById('city');
var districtSelect = document.getElementById('district');
var areaSelect = document.getElementById('street');
var openTime, closeTime, deadlineTime;
$(function () {
    // 最多两位小数
    $.validator.addMethod("isFloat2", function (value, element) {
        var score = /^[0-9]+\.?[0-9]{0,2}$/;
        return this.optional(element) || (score.test(value));
    }, "最多可输入两位小数");
    $.validator.addMethod("largeThan",function(value, element, param){
        var target = $('input[name='+param+']');
        // console.log(target.val());
        return value >= target.val();
    },$.validator.format("区间错误"));
    $.validator.addMethod("lessThan",function(value, element, param){
        var target = $('input[name='+param+']');
        // console.log(target.val());
        return value <= target.val();
    },$.validator.format("区间错误"));

    if (storeId != undefined && storeId != 0) {
        //如果是编辑，需要初始化地图定位等信息
        mapHandler.editInit();
    } else {
        map = new AMap.Map("addressMap", {
            resizeEnable: true,
            zoom: 5
        });
    }
    //初始化map
    mapHandler.init();

    $(document).on('click', '.amap-combo-close', function () {
        mapHandler.closeInfoWindow();
    }).on('click', '#J_setAddress', function () {
        $('#J_address').val($(this).attr('data-address'));
        $('input[name=lat]').val($(this).attr('data-lat'));
        $('input[name=lng]').val($(this).attr('data-lng'));
        mapHandler.closeInfoWindow();
    });

    //行政区划查询
    var opts = {
        subdistrict: 1,   //返回下一级行政区
        showbiz: false  //最后一级返回街道信息
    };
    district = new AMap.DistrictSearch(opts);
    district.search('中国', function (status, result) {
        if (status === 'complete') {
            mapHandler.getData(result.districtList[0]);
        }
    });

    //初始化时间空间
    openTime = $('input[name=openTime]').flatpickr({
        enableTime: true,
        noCalendar: true,
        enableSeconds: false,
        time_24hr: true,
        dateFormat: "H:i",
        minuteIncrement: 15,
        locale: 'zh',
        allowInput:true,
        defaultMinute: 0
    });
    closeTime = $('input[name=closeTime]').flatpickr({
        enableTime: true,
        noCalendar: true,
        enableSeconds: false,
        time_24hr: true,
        dateFormat: "H:i",
        minuteIncrement: 15,
        locale: 'zh',
        allowInput:true,
        defaultMinute: 0
    });
    deadlineTime = $('input[name=deadlineTime]').flatpickr({
        enableTime: true,
        noCalendar: true,
        enableSeconds: false,
        time_24hr: true,
        dateFormat: "H:i",
        minuteIncrement: 15,
        locale: 'zh',
        allowInput:true,
        defaultMinute: 0
    });
});
var mapHandler = {
    init: function () {
        AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], function () {
            var autoOptions = {
                // city: "杭州",
                input: "J_address"
            };
            autocomplete = new AMap.Autocomplete(autoOptions);
            var placeSearch = new AMap.PlaceSearch({
                type: '餐饮服务|购物服务|生活服务|住宿服务|商务住宅',
                // city: '杭州',
                map: map
            });
            AMap.event.addListener(autocomplete, "select", function (e) {
                placeSearch.search(e.poi.name)
            });

            $('#J_search').click(function () {
                var value = $.trim($('#J_address').val());
                if (value) placeSearch.search(value);
            });

            AMap.event.addListener(placeSearch, "markerClick", function (e) {
                var infoContent = mapHandler.createInfoWindow(e.data);
                var infoWindow = new AMap.InfoWindow({
                    isCustom: true,
                    content: infoContent,
                    offset: new AMap.Pixel(0, -30)
                });

                infoWindow.open(map, e.marker.getPosition());
            });

        });
        //增加标记的右键菜单
        contextMenu.addMarker = new AMap.ContextMenu();  //创建右键菜单
        //右键添加Marker标记
        contextMenu.addMarker.addItem("添加标记", function (e) {
            //判断该点是否在配送区域范围
            if (!regionPolygon.contains(contextMenuMarker.lnglat)) {
                layer.msg('不在配送区域');
                return;
            }
            var newMarker = new AMap.Marker({
                map: sendAreaMap,
                position: contextMenuMarker.lnglat, //基点位置
                title: regionMarkerIndex,
                content: '<div class="marker-route">' + regionMarkerIndex + '</div>'
            }).on('rightclick', mapHandler.removeMarkerListenFn);
            regionMarkerPosition[regionMarkerIndex] = {};
            regionMarkerPosition[regionMarkerIndex]['number'] = regionMarkerIndex;
            regionMarkerPosition[regionMarkerIndex]['lngLat'] = newMarker.getPosition();
            regionMarkerIndex++;
        }, 1);
        //删除标记的右键菜单
        contextMenu.removeMarker = new AMap.ContextMenu();  //创建右键菜单
        contextMenu.removeMarker.addItem("删除标记", function (e) {
            //todo 判断是否有区域使用到该点
            var markerNum = contextMenuMarker.target.getContentDom().getAttribute('title');
            if (regionMarkerPosition[markerNum]['isUsed'] != undefined && regionMarkerPosition[markerNum]['isUsed'] > 0) {
                layer.msg('该标记已被使用，不能删除');
                return;
            }
            if (regionMarkerPosition[markerNum]['deletable'] != undefined && regionMarkerPosition[markerNum]['deletable'] == false) {
                layer.msg('该标记为门店配送范围无法删除');
                return;
            }
            sendAreaMap.remove(contextMenuMarker.target);
        }, 1);
    },
    //如果是编辑，需要初始化一些控件
    editInit: function () {
        //设置校验规则
        $("#shopMoreInfo").validate({
            ignore: '',
            rules: {
                distributionRegions: {
                    required: true
                },
                distributionDivisionRegions: {
                    required: true
                },
                deliveryCost: {
                    required: true,
                    number: true,
                    isFloat2: true
                },
                minCost: {
                    required: true,
                    number: true,
                    isFloat2: true
                },
                freeCost: {
                    required: true,
                    number: true,
                    isFloat2: true
                }
            },
            messages: {
                distributionRegions: {
                    required: "请设置配送范围",
                },
                distributionDivisionRegions: {
                    required: "请划分配送区域"
                },
                deliveryCost: {
                    required: "请输入配送费用",
                    number: "请填写正确金额"
                },
                minCost: {
                    required: "请输入配送费用",
                    number: "请填写正确金额"
                },
                freeCost: {
                    required: "请输入配送费用",
                    number: "请填写正确金额"
                }
            }
        });
        $(".colorpicker-component").colorpicker();
        //初始化省市区
        selectorHandler.jsSelectItemByValue($("#province")[0], provinceCode);
        selectorHandler.jsSelectItemByValue($("#city")[0], cityCode);
        selectorHandler.jsSelectItemByValue($("#district")[0], districtCode);
        //初始化两张地图和门店位置
        map = new AMap.Map("addressMap", {
            resizeEnable: true,
            center: [lng, lat],
            zoom: 15
        });
        new AMap.Marker({
            position: [lng, lat],
            map: map
        });
        sendAreaMap = new AMap.Map("sendArea", {
            resizeEnable: true,
            center: [lng, lat],
            zoom: 15,
        });
        storeMarker = new AMap.Marker({
            position: [lng, lat],
            map: sendAreaMap,
            title: 1,
            content: '<div class="marker-route">1</div>'
        });
        //初始化配送区域
        if (!!distributionRegions) {
            var arr = lngLat2Arr(distributionRegions);
            //定义折线对象
            regionPolygon = new AMap.Polygon({
                map: sendAreaMap,
                path: arr,     //设置折线的节点数组
                strokeColor: "#3366FF", //线颜色
                strokeOpacity: 0.2, //线透明度
                strokeWeight: 3,    //线宽
                fillColor: "#1791fc", //填充色
                fillOpacity: 0.35//填充透明度
            });
            editor.polylineEditor = new AMap.PolyEditor(sendAreaMap, regionPolygon);
            $("input[name=distributionRegions]").val(JSON.stringify(distributionRegions));
        }
        //初始化划分区域点标记 regionMarkerPosition,regionMarkerIndex
        if (!!distributionMarkers && distributionMarkers.length > 0) {
            for (var i = 0; i < distributionMarkers.length; i++) {
                //1.赋值
                regionMarkerPosition[distributionMarkers[i].number] = {};
                regionMarkerPosition[distributionMarkers[i].number]['number'] = distributionMarkers[i].number;
                regionMarkerPosition[distributionMarkers[i].number]['deletable'] = distributionMarkers[i].deletable;
                regionMarkerPosition[distributionMarkers[i].number]['lngLat'] = distributionMarkers[i].lngLat;
                if (distributionMarkers[i].number > regionMarkerIndex) {
                    regionMarkerIndex = distributionMarkers[i].number;
                }
                //2.在地图上画出来
                var regionMarker = new AMap.Marker({
                    position: [distributionMarkers[i].lngLat.lng, distributionMarkers[i].lngLat.lat],
                    map: sendAreaMap,
                    title: distributionMarkers[i].number,
                    content: '<div class="marker-route">' + distributionMarkers[i].number + '</div>'
                });
                regionMarkers.push(regionMarker);
                // regionMarkers[distributionMarkers[i].number]=regionMarker;
            }
            //3.把对象赋值到input中
            $("input[name=distributionMarkers]").val(JSON.stringify(regionMarkerPosition));
            regionMarkerIndex++;
        } else {
            regionMarkerPosition[1] = {};
            regionMarkerPosition[1]['number'] = 1;
            regionMarkerPosition[1]['deletable'] = 0;
            regionMarkerPosition[1]['lngLat'] = storeMarker.getPosition();
        }
        //初始化划分区域 regionRegionDivision,RegionListObj
        if (!!distributionDivisionRegions && distributionDivisionRegions.length > 0) {
            for (var i = 0; i < distributionDivisionRegions.length; i++) {
                //在地图上画出该区域
                var newPolygon = new AMap.Polygon({
                    map: sendAreaMap,
                    path: lngLat2Arr(distributionDivisionRegions[i].distributionRegions),     //设置折线的节点数组
                    strokeColor: distributionDivisionRegions[i].color, //线颜色
                    strokeOpacity: 0.5, //线透明度
                    strokeWeight: 3,    //线宽
                    fillColor: distributionDivisionRegions[i].color, //填充色
                    fillOpacity: 0.5//填充透明度
                });
                regionRegionDivision[distributionDivisionRegions[i].id] = newPolygon;
                //设置点标记的 isUsed
                RegionListObj[distributionDivisionRegions[i].id] = {};
                RegionListObj[distributionDivisionRegions[i].id]['id'] = distributionDivisionRegions[i].id;
                RegionListObj[distributionDivisionRegions[i].id]['name'] = distributionDivisionRegions[i].name;
                RegionListObj[distributionDivisionRegions[i].id]['markerNum'] = distributionDivisionRegions[i].markerNum;
                RegionListObj[distributionDivisionRegions[i].id]['color'] = distributionDivisionRegions[i].color;
                var regionItemArea = getRegionArea(distributionDivisionRegions[i].markerNum);
                RegionListObj[distributionDivisionRegions[i].id]['distributionRegions'] = tmpPosition(regionItemArea);
            }
            $("input[name=distributionDivisionRegions]").val(JSON.stringify(RegionListObj));
        }

    },
    //构建自定义信息窗体
    createInfoWindow: function (data) {
        var addressDetail = (!!data.pname ? data.pname : '')
            + (!!data.cityname ? data.cityname : '')
            + (!!data.adname ? data.adname : '')
            + (!!data.address ? data.address : '');
        if (!!data.pname) {
            selectorHandler.jsSelectItemByValue($("#province")[0], data.pname);
        }
        if (!!data.cityname) {
            selectorHandler.jsSelectItemByValue($("#city")[0], data.cityname);
        }
        if (!!data.adname) {
            selectorHandler.jsSelectItemByValue($("#district")[0], data.adname);

        }
        var $wrap = $('<div></div>');
        var $div = '<div class="amap-content-body">\n' +
            '            <div class="amap-lib-infowindow">\n' +
            '                <div class="amap-lib-infowindow-title">\n' +
            '                    <span>' + data.name + '</span>\n' +
            '                    <div>地址：' + addressDetail + '</div>\n' +
            '                    <div>类型：' + data.type + '</div>\n' +
            '                </div>\n' +
            '                <div class="amap-lib-infowindow-content">\n' +
            '                    <div class="amap-lib-infowindow-content-wrap">\n' +
            '                        <a href="javascript:;" class="btn-link" id="J_setAddress" data-lat="' + data.location.lat + '" data-lng="' + data.location.lng + '" data-address="' + addressDetail + '">设为联系地址</a>\n' +
            '                    </div>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '        <div class="amap-combo-close"></div>\n' +
            '        <div class="amap-combo-sharp"></div>';
        $wrap.append($div);
        return $wrap[0];
    },
    //关闭信息窗体
    closeInfoWindow: function () {
        map.clearInfoWindow();
    },
    getData: function (data, level) {
        var bounds = data.boundaries;
        if (bounds) {
            for (var i = 0, l = bounds.length; i < l; i++) {
                var polygon = new AMap.Polygon({
                    map: map,
                    strokeWeight: 1,
                    strokeColor: '#CC66CC',
                    fillColor: '#CCF3FF',
                    fillOpacity: 0.5,
                    path: bounds[i]
                });
                polygons.push(polygon);
            }
            map.setFitView();//地图自适应
        }


        //清空下一级别的下拉列表
        if (level === 'province') {
            citySelect.innerHTML = '';
            districtSelect.innerHTML = '';
            areaSelect.innerHTML = '';
        } else if (level === 'city') {
            districtSelect.innerHTML = '';
            areaSelect.innerHTML = '';
        } else if (level === 'district') {
            areaSelect.innerHTML = '';
        }

        var subList = data.districtList;
        if (subList) {
            var contentSub = new Option('--请选择--');
            var curlevel = subList[0].level;
            var curList = document.querySelector('#' + curlevel);
            curList.add(contentSub);
            for (var i = 0, l = subList.length; i < l; i++) {
                var name = subList[i].name;
                var levelSub = subList[i].level;
                var cityCode = subList[i].citycode;
                contentSub = new Option(name);
                contentSub.setAttribute("value", levelSub);
                contentSub.center = subList[i].center;
                contentSub.adcode = subList[i].adcode;
                curList.add(contentSub);
            }
        }
    },
    changeCity: function (obj) {
        //清除地图上所有覆盖物
        for (var i = 0, l = polygons.length; i < l; i++) {
            polygons[i].setMap(null);
        }
        var option = obj[obj.options.selectedIndex];
        var keyword = option.text; //关键字
        var adcode = option.adcode;
        district.setLevel(option.value); //行政区级别
        district.setExtensions('all');
        //行政区查询
        //按照adcode进行查询可以保证数据返回的唯一性
        district.search(adcode, function (status, result) {
            if (status === 'complete') {
                mapHandler.getData(result.districtList[0], obj.id);
            }
        });
    },
    setCenter: function (obj) {
        map.setCenter(obj[obj.options.selectedIndex].center);
    },
    addMarkerListenFn: function (e) {
        contextMenu.addMarker.open(sendAreaMap, e.lnglat);
        contextMenuMarker = e;
    },
    removeMarkerListenFn: function (e) {
        contextMenu.removeMarker.open(sendAreaMap, e.lnglat);
        contextMenuMarker = e;
    },
    regionOption: function (obj) {
        var showClass = $(obj).attr('data-show');
        if ('J_RegionDistribution'.indexOf(showClass) > -1) {
            //门店配送范围
            if (Object.keys(RegionListObj).length > 0) {
                layer.msg("已设置配送区域，无法编辑门店");
                return;
            }
            if (!!editor.polylineEditor) {
                editor.polylineEditor.open();
            } else {
                mouseTool = new AMap.MouseTool(sendAreaMap);
                mouseTool.polygon();
                mouseTool.on('draw', function (type, obj) {
                    sendAreaMap.plugin(["AMap.PolyEditor"], function () {
                        editor.polylineEditor = new AMap.PolyEditor(sendAreaMap, type.obj);
                        editor.polylineEditor.open();
                        mouseTool.close();
                    });
                });
            }
        } else if ('J_RegionDivision'.indexOf(showClass) > -1) {
            //配送区域划分
            //如果门店配送范围多边形还未设置，就先设置
            if (regionPolygon == undefined) {
                layer.msg('请先设置门店配送范围');
                return;
            }

            sendAreaMap.on('rightclick', mapHandler.addMarkerListenFn);
            for (var i = 0; i < sendAreaMap.getAllOverlays("polygon").length; i++) {
                // console.log(sendAreaMap.getAllOverlays("polygon")[i]);
                sendAreaMap.getAllOverlays("polygon")[i].on('rightclick', mapHandler.addMarkerListenFn);
            }
            for (var i = 0; i < sendAreaMap.getAllOverlays('marker').length; i++) {
                sendAreaMap.getAllOverlays("marker")[i].on('rightclick', mapHandler.removeMarkerListenFn);
            }
        } else {
            editor.polylineEditor.close();
        }
        $('#' + showClass).removeClass('displayNone').siblings().addClass('displayNone');
    },
    saveRegionDivision: function (obj) {
        //判断是否有配送区域
        //保存点标记和配送区域
        $("input[name=distributionMarkers]").val(JSON.stringify(regionMarkerPosition));
        $("input[name=distributionDivisionRegions]").val(JSON.stringify(RegionListObj));
        this.regionOption(obj);
        for (var i = 0; i < sendAreaMap.getAllOverlays("polygon").length; i++) {
            // console.log(sendAreaMap.getAllOverlays("polygon")[i]);
            sendAreaMap.getAllOverlays("polygon")[i].off('rightclick', mapHandler.addMarkerListenFn);
        }
        sendAreaMap.off('rightclick', mapHandler.addMarkerListenFn);
        for (var i = 0; i < sendAreaMap.getAllOverlays('marker').length; i++) {
            sendAreaMap.getAllOverlays("marker")[i].off('rightclick', mapHandler.removeMarkerListenFn);
        }
    }
    ,
    saveRegionDistribution: function (obj) {
        //保存前先判断点是否在区域内
        regionPolygon = editor.polylineEditor.cf;
        if (!regionPolygon.contains(storeMarker.getPosition())) {
            layer.msg('门店不在该区域内');
            return;
        }
        //删除所有的点
        for (var i = 0; i < regionMarkers.length; i++) {
            sendAreaMap.remove(regionMarkers[i]);
        }
        regionMarkers = [];
        regionMarkerIndex = 2;
        var distributionRegions = [];
        var regionPath = regionPolygon.getPath();
        for (var i = 0; i < regionPath.length; i++) {

            var lngLatObj = {};
            lngLatObj['lng'] = regionPath[i].getLng();
            lngLatObj['lat'] = regionPath[i].getLat();
            distributionRegions.push(lngLatObj);
            regionMarkerPosition[regionMarkerIndex] = {};
            regionMarkerPosition[regionMarkerIndex]['number'] = regionMarkerIndex;
            regionMarkerPosition[regionMarkerIndex]['lngLat'] = regionPath[i];
            regionMarkerPosition[regionMarkerIndex]['deletable'] = 0;

            //这些点是不允许删除的
            var newRegionMarker = new AMap.Marker({
                map: sendAreaMap,
                position: regionPath[i],
                title: regionMarkerIndex,
                content: '<div class="marker-route">' + regionMarkerIndex + '</div>'
            });
            regionMarkers.push(newRegionMarker);
            // regionMarkers[regionMarkerIndex] = newRegionMarker;
            regionMarkerIndex++;
        }
        $("input[name=distributionRegions]").val(JSON.stringify(distributionRegions));
        editor.polylineEditor.close();
        this.regionOption(obj);
    }
};
//转为经纬度坐标数组
function lngLat2Arr(distributionRegions) {
    var arr = [];//经纬度坐标数组
    for (var i = 0; i < distributionRegions.length; i++) {
        arr.push(Array(distributionRegions[i].lng, distributionRegions[i].lat));
    }
    return arr;
}

var editShopHandler = {
    saveShopBasicInfo: function () {
        if ($("#shopBasicInfo").valid()) {
            var data = $('#shopBasicInfo').serializeObject();
            var provinceCode = $("#province option:selected").text();
            var cityCode = $("#city option:selected").text();
            var districtCode = $("#district option:selected").text();
            data['provinceCode'] = provinceCode;
            data['cityCode'] = cityCode;
            data['districtCode'] = districtCode;
            if (!!storeId) {
                data['storeId'] = storeId;
            }
            layer.load();
            $.ajax(baseUrl + "mall/store/saveStoreBaseInfo", {
                method: 'POST',
                data: data,
                dataType: 'json',
                success: function (data) {
                    layer.closeAll('loading');
                    if (data.code != 200) {
                        layer.msg(data.data);
                    } else {
                        layer.msg('保存成功');
                        var storeId = data.data;
                        setTimeout(function () {
                            window.location.href = baseUrl + "mall/store/edit?storeId=" + storeId;
                        }, 300);
                    }
                },
                error: function () {
                    layer.closeAll('loading');
                    layer.msg("系统错误");
                }
            })

        }
    },
    saveShopMoreInfo: function () {
        //判断一下配送范围和划分区域是否已经保存了
        if(!$("#J_RegionDistribution").hasClass("displayNone")){
            layer.msg('请保存配送范围');
            return;
        }
        if(!$("#J_RegionDivision").hasClass("displayNone")){
            layer.msg('请保存划分区域');
            return;
        }
        if ($("#shopMoreInfo").valid()) {
            //判断是否有区域
            if (Object.keys(RegionListObj).length == 0) {
                layer.msg('请添加配送区域');
                return;
            }
            layer.load();
            var data = $('#shopMoreInfo').serializeObject();
            data['storeId'] = storeId;
            $.ajax(baseUrl + "mall/store/saveStoreMoreInfo", {
                method: 'POST',
                data: data,
                dataType: 'json',
                success: function (data) {
                    layer.closeAll('loading');
                    if (data.code != 200) {
                        layer.msg(data.data);
                    } else {
                        layer.msg('保存成功');
                        setTimeout(function () {
                            window.location.reload()
                        }, 300);
                    }
                },
                error: function () {
                    layer.closeAll('loading');
                    layer.msg("系统错误");
                }
            })
        }
    },
    returnToList: function(){
        window.location.href = baseUrl + "mall/store/list";
    }
};

var uploadHandler = {
    uploadImg: function (btnFile, showImgId, pathId) {
        layer.load();
        $.ajaxFileUpload({
            url: baseUrl + "mall/common/upload",
            secureuri: false,//安全协议
            fileElementId: btnFile,//id
            dataType: 'json', //返回值类型 一般设置为json
            type: 'post',
            success: function (json) {
                layer.closeAll('loading');
                if (json.result == 1) {
                    $("#" + showImgId).attr("src", json.fileUrl);
                    $("#" + pathId).val(json.fileUri);
                    layer.msg('上传成功');
                } else {
                    layer.msg("上传失败");
                }
            },
            error: function () {
                layer.closeAll('loading');
            }
        });
    }
}

var selectorHandler = {
    //设置select中text="paraText"的第一个Item为选中
    jsSelectItemByValue: function (objSelect, objItemText) {
        //判断是否存在
        var isExit = false;
        for (var i = 0; i < objSelect.options.length; i++) {
            if (objSelect.options[i].text == objItemText) {
                objSelect.options[i].selected = true;
                isExit = true;
                break;
            }
        }
        if (!isExit) {
            this.jsAddItemToSelect(objSelect, objItemText, objSelect.id);
        }
    },
    //向select选项中 加入一个Item,并选中
    jsAddItemToSelect: function (objSelect, objItemText, objItemValue) {
        //判断是否存在
        var varItem = new Option(objItemText, objItemValue);
        varItem.selected = true;
        objSelect.options.add(varItem);
    }
};

var RegionListObj = {};
function getRegionArea(markerStr) {
    var markers = markerStr.split(",");
    var position = [];
    for (var i = 0; i < markers.length; i++) {
        position.push(regionMarkerPosition[markers[i]]['lngLat']);
        if (regionMarkerPosition[markers[i]]['isUsed'] == undefined) {
            regionMarkerPosition[markers[i]]['isUsed'] = 1;
        } else {
            regionMarkerPosition[markers[i]]['isUsed']++;
        }
    }
    return position;
}
function delRegionArea(markerStr) {
    if (markerStr == undefined || markerStr.length == 0) {
        return null;
    }
    var markers = markerStr.split(",");
    var position = [];
    for (var i = 0; i < markers.length; i++) {
        regionMarkerPosition[markers[i]]['isUsed']--;
    }
    return position;
}
// 模拟API
function tmpPosition(positionArr) {
    var position = [];
    for (var i = 0; i < positionArr.length; i++) {
        position.push(positionArr[i]);
    }
    return position;
}
var Region = {
    id: 0,
    index: Object.keys(RegionListObj).length,
    tpl: function (id, index) {
        return '<tr data-id="' + id + '">' +
            '    <td><input type="text" value="区域名称' + index + '" class="form-control"></td>' +
            '    <td><input type="text" placeholder="区域范围" class="form-control"></td>' +
            '    <td><div class="input-group colorpicker-component"><input type="hidden" value="#3366FF" placeholder="区域范围" class="form-control"><span class="input-group-addon"><i></i></span></div></td>' +
            '    <td>' +
            '        <button type="button" class="btn btn-success btn-xs js-regionItemEdit displayNone">编辑</button>' +
            '        <button type="button" class="btn btn-success btn-xs js-regionItemSave">保存</button>' +
            '        <button type="button" class="btn btn-default btn-xs js-regionItemDel">删除</button>' +
            '    </td>' +
            '</tr>';
    },
    addList: function () {
        var self = this;
        var regionList = $('#J_regionList');
        $('#J_addRegionItem').click(function () {
            self.index += 1;
            self.id -= 1;
            var tr = self.tpl(self.id, self.index);
            regionList.append(tr);
            $(".colorpicker-component").colorpicker();
        });
    },
    save: function () {
        $(document).on('click', '.js-regionItemSave', function () {
            $(this).addClass('displayNone').siblings('.js-regionItemEdit').removeClass('displayNone');
            var parent = $(this).closest('tr');
            var nameInput = parent.find('input').eq(0),
                valueInput = parent.find('input').eq(1),
                colorInput = parent.find('input').eq(2);
            var id = parent.attr('data-id');
            if(nameInput.val().length == 0 || valueInput.val().length == 0){
                layer.msg('请输入区域名称和区域范围');
                return;
            }
            RegionListObj[id] = {};
            RegionListObj[id]['name'] = nameInput.val();
            RegionListObj[id]['markerNum'] = valueInput.val();
            RegionListObj[id]['color'] = colorInput.val();
            var regionItemArea = getRegionArea(RegionListObj[id]['markerNum']);
            // console.log(regionItemArea);
            // console.log(lngLat2Arr(regionItemArea));
            RegionListObj[id]['distributionRegions'] = tmpPosition(regionItemArea);
            nameInput.prop('readonly', true);
            valueInput.prop('readonly', true);
            colorInput.prop('readonly', true);
            colorInput.prop('disabled', true);
            // console.info(RegionListObj);
            //如果已经存在区域，先把这个区域删除
            if (regionRegionDivision[id] != undefined) {
                sendAreaMap.remove(regionRegionDivision[id]);
            }
            //在地图上画出该区域
            var newPolygon = new AMap.Polygon({
                map: sendAreaMap,
                path: lngLat2Arr(regionItemArea),     //设置折线的节点数组
                strokeColor: '' + colorInput.val(), //线颜色
                strokeOpacity: 0.5, //线透明度
                strokeWeight: 3,    //线宽
                fillColor: '' + colorInput.val(), //填充色
                fillOpacity: 0.5//填充透明度
            }).on('rightclick', mapHandler.addMarkerListenFn);
            ;
            regionRegionDivision[id] = newPolygon;
        });
    },
    edit: function () {
        $(document).on('click', '.js-regionItemEdit', function () {
            $(this).addClass('displayNone').siblings('.js-regionItemSave').removeClass('displayNone');
            var input = $(this).closest('tr').find('input');
            input.prop('readonly', false);
            input.prop('disabled', false);
            var markerNum = $(this).closest('tr').find('input').eq(1);
            delRegionArea(markerNum.val());

        });
    },
    delete: function () {
        $(document).on('click', '.js-regionItemDel', function () {
            var parent = $(this).closest('tr');
            var id = parent.attr('data-id');
            var markerNum = parent.find('input').eq(1);
            delRegionArea(markerNum.val());
            parent.remove();
            delete RegionListObj[id];
            if(regionRegionDivision[id] != undefined){
                sendAreaMap.remove(regionRegionDivision[id]);
            }
            // console.info(RegionListObj);
        });
    },
    init: function () {
        this.addList();
        this.save();
        this.edit();
        this.delete();
    }
};
Region.init();

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};