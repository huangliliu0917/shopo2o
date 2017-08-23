/**
 * Created by admin on 2017-08-22.
 */
var map, district, polygons = [], citycode;
var citySelect = document.getElementById('city');
var districtSelect = document.getElementById('district');
var areaSelect = document.getElementById('street');
var openTime,closeTime,deadlineTime;
$(function () {
    map = new AMap.Map("addressMap", {
        resizeEnable: true,
        zoom: 3
    });
    AMap.plugin(['AMap.Autocomplete', 'AMap.PlaceSearch'], function () {
        var autoOptions = {
            city: "杭州", //城市，默认全国
            input: "address"//使用联想输入的input的id
        };
        autocomplete = new AMap.Autocomplete(autoOptions);
        var placeSearch = new AMap.PlaceSearch({
            type: '餐饮服务|购物服务|生活服务|住宿服务|商务住宅',
            city: '杭州',
            map: map
        });
        AMap.event.addListener(autocomplete, "select", function (e) {
            //TODO 针对选中的poi实现自己的功能
            console.log(e);
            placeSearch.search(e.poi.name)
        });
    });
    //行政区划查询
    var opts = {
        subdistrict: 1,   //返回下一级行政区
        showbiz: false  //最后一级返回街道信息
    };
    district = new AMap.DistrictSearch(opts);//注意：需要使用插件同步下发功能才能这样直接使用
    district.search('中国', function (status, result) {
        if (status == 'complete') {
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
    openTime.config.onChange =function(dateobj, datestr){
        console.info(dateobj, datestr);
        console.info(specific_calendar.input);
    }
});
var mapHandler = {
    search: function () {
        placeSearch.search(e.poi.name)
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
    }
}

var uploadHandler = {
    uploadImg: function (btnFile, showImgId, pathId) {
        layer.load();

        $.ajaxFileUpload({
            url:baseUrl + "mall/common/upload",
            data:{customerId:$('input[name=customerId]').val()},
            secureuri: false,//安全协议
            fileElementId: btnFile,//id
            dataType: 'json', //返回值类型 一般设置为json
            success: function (json) {
                if (json.result == 1) {
                    $("#" + showImgId).attr("src", json.fileUrl);
                    $("#" + pathId).val(json.fileUri);
                    layer.msg('上传成功');
                } else {
                    layer.msg("上传失败");
                }
            },
            error:function(){
                layer.closeAll('loading');
            }
        });
    }
}
