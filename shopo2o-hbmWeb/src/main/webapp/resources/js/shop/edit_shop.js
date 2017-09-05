/**
 * Created by admin on 2017-08-22.
 */
var map, sendAreaMap, district, polygons = [];
var mouseTool; //画多边形
var polylineEditor;//多边形编辑
var regionPolygon; //配送范围多边形
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

    if (storeId != undefined && storeId != 0) {
        //如果是编辑，需要初始化地图定位等信息
        //配送规则校验
        $("#shopMoreInfo").validate({
            ignore: '',
            rules: {
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
        selectorHandler.jsSelectItemByValue($("#province")[0],provinceCode);
        selectorHandler.jsSelectItemByValue($("#city")[0],cityCode);
        selectorHandler.jsSelectItemByValue($("#district")[0],districtCode);
        map = new AMap.Map("addressMap", {
            resizeEnable: true,
            center: [lan, lat],
            zoom: 15
        });
        new AMap.Marker({
            position: [lan, lat]
        }).setMap(map);
        sendAreaMap = new AMap.Map("sendArea", {
            resizeEnable: true,
            center: [lan, lat],
            zoom: 15
        });
        new AMap.Marker({
            position: [lan, lat]
        }).setMap(sendAreaMap);
        if (!!distributionRegions) {
            var arr = [];//经纬度坐标数组
            for (var i = 0; i < distributionRegions.length; i++) {
                arr.push(Array(distributionRegions[i].lan, distributionRegions[i].lat));
            }
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
            polylineEditor = new AMap.PolyEditor(sendAreaMap, regionPolygon);
        }
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
        $('input[name=lan]').val($(this).attr('data-lng'));
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
        // allowInput:true,
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
        // allowInput:true,
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
        // allowInput:true,
        defaultMinute: 0
    });
    openTime.config.onChange = function (dateobj, datestr) {
        console.info(dateobj, datestr);
        console.info(specific_calendar.input);
    }

    $("#shopBasicInfo").validate({
        ignore: ''
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
    regionOption: function (obj) {
        var showClass = $(obj).attr('data-show');
        $('#' + showClass).removeClass('displayNone').siblings().addClass('displayNone');
        if ('J_RegionDistribution'.indexOf(showClass) > -1) {
            //门店配送范围
            if (!!polylineEditor) {
                polylineEditor.open();
            } else {
                mouseTool = new AMap.MouseTool(sendAreaMap);
                mouseTool.polygon();
                mouseTool.on('draw', function (type, obj) {
                    sendAreaMap.plugin(["AMap.PolyEditor"], function () {
                        polylineEditor = new AMap.PolyEditor(sendAreaMap, type.obj);
                        polylineEditor.open();
                        mouseTool.close();
                    });
                });
            }
        } else if ('J_RegionDivision'.indexOf(showClass) > -1) {
            //配送区域划分
            mouseTool = new AMap.MouseTool(sendAreaMap);
            mouseTool.polygon();
            mouseTool.on('draw', function (type, polygonObj) {
                //计算有效区域
                var validPath = [];
                var path = type.obj.getPath(), regionPath = regionPolygon.getPath();
                //画出的点是否在区域内
                for (var i = 0; i < path.length; i++) {
                    if (regionPolygon.contains(path[i])) {
                        validPath.push(path[i]);
                    }
                }
                //配送范围的哪些点再画出的区域内
                for (var j = 0; j < regionPath.length; j++) {
                    if (type.obj.contains(regionPath[j])) {
                        validPath.push(regionPath[j]);
                    }
                }
                type.obj.setPath(validPath);
                sendAreaMap.plugin(["AMap.PolyEditor"], function () {
                    polylineEditor = new AMap.PolyEditor(sendAreaMap, type.obj);
                    polylineEditor.open();
                    mouseTool.close();
                });
            });
        } else {
            polylineEditor.close();
        }
    },
    regionDivision: function (obj) {
        console.log(RegionListObj);
    },
    saveRegionDistribution: function (obj) {
        //保存前先判断点是否在区域内
        var marker = sendAreaMap.getAllOverlays('marker')[0];
        var region = sendAreaMap.getAllOverlays('polygon')[0];
        if (!region.contains(marker.getPosition())) {
            layer.msg('门店不在该区域内');
            return;
        }
        var distributionRegions = [];
        var regionPath = region.getPath();
        for (var i = 0; i < regionPath.length; i++) {

            var lngLatObj = {};
            lngLatObj['lan'] = regionPath[i].getLng();
            lngLatObj['lat'] = regionPath[i].getLat();
            distributionRegions.push(lngLatObj);
        }
        $("input[name=distributionRegions]").val(JSON.stringify(distributionRegions));
        polylineEditor.close();
        this.regionOption(obj);
    }
};

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
    saveShopMoreInfo: function () {
        if ($("#shopMoreInfo").valid()) {
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

// 模拟API
function tmpPosition() {
    return [
        {lan:1,lat:2},
        {lan:11,lat:21},
        {lan:12,lat:22},
        {lan:13,lat:23},
        {lan:14,lat:24}
    ]
}
var Region = {
    id: 0,
    index: Object.keys(RegionListObj).length,
    tpl: function (id, index) {
        return '<tr data-id="' + id + '">' +
            '    <td><input type="text" value="配送区域' + index + '" class="form-control"></td>' +
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
        });
    },
    save: function () {
        $(document).on('click', '.js-regionItemSave', function () {
            $(this).addClass('displayNone').siblings('.js-regionItemEdit').removeClass('displayNone');
            var parent = $(this).closest('tr');
            var input = parent.find('input');
            var id = parent.attr('data-id');
            RegionListObj[id] = {};
            RegionListObj[id]['name'] = input.val();
            // 临时方案
            RegionListObj[id]['position'] = tmpPosition();
            input.prop('readonly', true);
            console.info(RegionListObj);
        });
    },
    edit: function () {
        $(document).on('click', '.js-regionItemEdit', function () {
            $(this).addClass('displayNone').siblings('.js-regionItemSave').removeClass('displayNone');
            var input = $(this).closest('tr').find('input');
            input.prop('readonly', false);
        });
    },
    delete: function () {
      $(document).on('click', '.js-regionItemDel', function () {
          var parent = $(this).closest('tr');
          var id = parent.attr('data-id');
          parent.remove();
          delete RegionListObj[id];
          console.info(RegionListObj);
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