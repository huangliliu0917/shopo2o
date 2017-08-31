/**
 * Created by admin on 2017-08-22.
 */
var map, district, polygons = [], citycode;
var citySelect = document.getElementById('city');
var districtSelect = document.getElementById('district');
var areaSelect = document.getElementById('street');
var openTime, closeTime, deadlineTime;
$(function () {
    map = new AMap.Map("addressMap", {
        resizeEnable: true,
        zoom: 3
    });


    AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], function () {
        var autoOptions = {
            city: "杭州",
            input: "J_address"
        };
        autocomplete = new AMap.Autocomplete(autoOptions);
        var placeSearch = new AMap.PlaceSearch({
            type: '餐饮服务|购物服务|生活服务|住宿服务|商务住宅',
            city: '杭州',
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
            var infoContent = createInfoWindow(e.data);
            var infoWindow = new AMap.InfoWindow({
                isCustom: true,
                content: infoContent,
                offset: new AMap.Pixel(0, -30)
            });

            infoWindow.open(map, e.marker.getPosition());
        });

    });

    //构建自定义信息窗体
    function createInfoWindow(data) {
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
    }

    $(document).on('click', '.amap-combo-close', function () {
        closeInfoWindow();
    }).on('click', '#J_setAddress', function () {
        $('#J_address').val($(this).attr('data-address'));
        $('input[name=lat]').val($(this).attr('data-lat'));
        $('input[name=lan]').val($(this).attr('data-lng'));
        closeInfoWindow();
    });

    //关闭信息窗体
    function closeInfoWindow() {
        map.clearInfoWindow();
    }

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
    })
});
var mapHandler = {
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
    }
}

var addShopHandler = {
    saveShopBasicInfo: function () {
        if ($("#shopBasicInfo").valid()) {
            $.showLoading('正在保存');
            $.ajax(baseUrl + "hbmWeb/shop/saveShopBaseInfo", {
                method: 'POST',
                data: $('#shopBasicInfo').serializeObject(),
                dataType: 'json',
                success: function (data) {
                    $.hideLoading();
                    if (data.code != 200) {
                        $.toptip(data.data);
                    } else {
                        $.toptip('保存成功');
                        setTimeout(function () {
                            window.location.reload()
                        }, 300);
                    }
                },
                error: function () {
                    $.hideLoading();
                    $.toptip("系统错误");
                }
            })

        }
    }
}

var uploadHandler = {
    uploadImg: function (btnFile, showImgId, pathId) {
        layer.load();
        $.ajaxFileUpload({
            url: baseUrl + "mall/common/upload",
            secureuri: false,//安全协议
            fileElementId: "btnFile",//id
            dataType: 'json', //返回值类型 一般设置为json
            type: 'post',
            success: function (json) {
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
}

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
}
