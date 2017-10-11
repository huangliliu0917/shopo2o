/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

function returnTop(){
    var body = $('body');
    if (body.scrollTop() !== 100) {
        body.animate({
            scrollTop: 0
        }, 200);
    }

}
//规格编辑
var specProductEditor = {};

//#region 属性变量
specProductEditor.selectedSpecValues = [];
specProductEditor.currentGoodsTypeId = 0;
specProductEditor.sellInfo = {};
specProductEditor.currentSpecList = [];
specProductEditor.exsitProductRows = {};
specProductEditor.isChangeProduct = 0;

//初始化
specProductEditor.initSpec = function(list,bn){
    this.sellInfo['txtProductBN']=bn;
    if(list == null || list.length == 0){
        var noSpecRow = $('#tmplProductTableRow').html();
        noSpecRow = noSpecRow.replace("{$specs$}", "");
        noSpecRow = noSpecRow.replace("{$rowclass$}", "");
        noSpecRow = noSpecRow.replace("{$specids}", "").replace("{$specids}","");
        noSpecRow = noSpecRow.replace(/{specids}/g, "");
        $('#specProductList').append(noSpecRow);
        $("input[name='pro_bn']").val(bn);
    }else{
        this.currentSpecList = list;
    }
}

specProductEditor.initExistProductRow = function () {//修改前的货品行
    this.exsitProductRows = this.getRecordedRows();
    var arrTemp = [];
    //规格数量
    $("#divSpecList").children('div').each(function(i){
        arrTemp[i] = [];
        $(this).find("input[class~=js-specValue-check]:checked").each(function(){
            var objVal = [];
            objVal['specValue']=$(this).attr('specValue');
            objVal['specValueId']=$(this).attr('specValueId');
            objVal['specId']=$(this).attr('specId');
            var arrImages = specProductEditor.getExistGoodsImages($(this).attr('specValueId'));
            $("img[name=pro_spec_value_img_" +$(this).attr('specValueId') + "]").attr('src',(arrImages != null && arrImages.length > 0 ? arrImages[0].src : '/resource/images/dg2017.jpg'));
            arrTemp[i].push(objVal);
        });
    })
    this.selectedSpecValues = arrTemp;
    specProductEditor.updateProductTable();
};
specProductEditor.initNoSpecProduct = function(product){
    $('input[name="pro_bn"]').val(product[0].bn);
    $('input[name="pro_id"]').val(product[0].supplierProductId);
    $('input[name="pro_store"]').val(product[0].store);
    $('input[name="pro_mktPrice"]').val(product[0].mktPrice);
    $('input[name="pro_min_price"]').val(product[0].minPrice);
    // $('input[name="pro_max_price"]').val(product[0].maxPrice);
    $('input[name="pro_cost"]').val(product[0].cost);
    $('input[name="pro_weight"]').val(product[0].weight);
}

//选中规格
specProductEditor.getCheckedSpecValue = function (obj) {
    //显示或隐藏规格编辑链接
    if (obj.checked == true) {
        $(obj).siblings('input[name=spec_value_customer]').removeClass('dl');
        $(obj).siblings('i[class~=js-spec-images-link]').removeClass('dl');
        $(obj).parents('div[class~=xzh]').addClass('on');
    } else {
        $(obj).siblings('input[name=spec_value_customer]').addClass('dl');
        $(obj).siblings('i[class~=js-spec-images-link]').addClass('dl');
        $(obj).parents('div[class~=xzh]').removeClass('on');
    }
    var arrTemp = [];
    //规格数量
    $("#divSpecList").children('div').each(function(i){
        arrTemp[i] = [];
        $(this).find("input[class~=js-specValue-check]:checked").each(function(){
            var objVal = [];
            objVal['specValue']=$(this).attr('specValue');
            objVal['specValueId']=$(this).attr('specValueId');
            objVal['specId']=$(this).attr('specId');
            arrTemp[i].push(objVal);
        });
    })
    this.selectedSpecValues = arrTemp;
    specProductEditor.updateProductTable();
    //增加校验
    $("#specProductList input[name=pro_bn]").each(function(){
        $(this).rules('add',{required: true});
    })
    $("#specProductList input[name=pro_store]").each(function(){
        $(this).rules('add',{required:true,number:true,digits:true,min:0});
    })
    $("#specProductList input[name=pro_mktPrice]").each(function(){
        $(this).rules('add',{required:true,number:true,min:0});
    })
    $("#specProductList input[name=pro_min_price]").each(function(){
        $(this).rules('add',{required:true,number:true,min:0});
    })
    /*$("#specProductList input[name=pro_max_price]").each(function(){
        $(this).rules('add',{required:true,number:true,min:0,largeThan:'pro_min_price'});
    })*/
    $("#specProductList input[name=pro_cost]").each(function(){
        $(this).rules('add',{required:true,number:true,min:0});
    })
    $("#specProductList input[name=pro_weight]").each(function(){
        $(this).rules('add',{required:true,number:true,min:0});
    })
}

//显示/隐藏别名
specProductEditor.toggleValueAlias = function (flag, obj) {
    if (flag == true) {
        //显示
        $(obj).siblings('input[name=spec_value_customer]').removeClass('dl');
        $(obj).addClass('dl');
        $(obj).siblings('a[name=valAliasHide]').removeClass('dl');
    } else {
        //隐藏
        $(obj).siblings('input[name=spec_value_customer]').addClass('dl');
        $(obj).siblings('a[name=valAliasShow]').removeClass('dl');
        $(obj).addClass('dl');
    }
}

//修改别名
specProductEditor.changeSpecValueCustomer = function(obj){
    var parentDiv = $(obj).parents('div[class~=xzh]');
    var specValueId = $(parentDiv).attr('specValueId');
    var specValue = $(parentDiv).attr('specValue');
    var customerName = $(obj).val();
    if(customerName.length == 0){
        customerName = specValue;
    }
    $('span[name=txtpro_spec_value_'+specValueId+']').text(customerName);
    $('input[class=cls_spec_value_'+specValueId+']').val(customerName);

}

//搜索规格
specProductEditor.searchSpec = function (obj) {
    var searchKeyWord = $(obj).val();
    $(obj).parents('fieldset').find('div').children('div[class~=xzh]').each(function () {
        var specValue = $(this).attr('specValue');
        if (searchKeyWord.length > 0 && specValue.length > 0 && specValue.indexOf(searchKeyWord) > -1) {
            $(this).addClass('search');
        } else {
            $(this).removeClass('search');
        }
    })
}
specProductEditor.searchSpecV2 = function(obj){
    var searchKeyWord = $(obj).val();
    $(obj).parents('div[class~=ibox-content]').find('div').children('div[class~=xzh]').each(function () {
        var specValue = $(this).attr('specValue');
        if (searchKeyWord.length > 0 && specValue.length > 0 && specValue.indexOf(searchKeyWord) > -1) {
            $(this).addClass('search');
        } else {
            $(this).removeClass('search');
        }
    })
}

//点击上传图片
specProductEditor.relateSpecToImages = function (obj) {
    var specValueId = $(obj).attr('specValueId');
    var url = /*[[@{goods/specRelImages.html}]]*/'../goods/specRelImages.html'
    url = url + '?specvalueid=' + specValueId + '&rnd=' + Math.random();
    hot.iframeModalWithButton(url,"800px","470px","定义规格图片",function (index, layero) {
        var iframeWin = window[layero.find('iframe')[0]['name']];
        iframeWin.setImagesRelResult();
        var arrImages = specProductEditor.getExistGoodsImages(specValueId);
        $("img[name=pro_spec_value_img_" +specValueId + "]").attr('src',(arrImages != null && arrImages.length > 0 ? arrImages[0].src : '/resources/images/dg2017.jpg'));
        layer.close(index);
    });
    /*J.PopupIFrame(url, "定义规格图片", 800, 470, "product_menu22", {"确定": true}, "auto", "", function (result) {
    });*/
}

//获取目前的商品图片
specProductEditor.getCurrentGoodsImages = function () {
    var imgIds = [], imgPaths = [],imgSrcs = [];
    $('input[name="hidGoodsImageId"]').each(function () {
        imgIds.push(this.value);
    });
    $('input[name="hidGoodsImagePath"]').each(function () {
        var imgObj = JSON.parse(this.value.replace(/\'/g,"\""));
        imgPaths.push(imgObj['bigPic']);
        console.log(imgObj['bigPic']);
    });
    $('img[name="imgGoods"]').each(function(){
        imgSrcs.push(this.src);
    });
    return { ids: imgIds, paths: imgPaths,srcs: imgSrcs };
};

//获取已经选择过的商品图片
specProductEditor.getExistGoodsImages = function (specValueId) {
    var existImages = $('div[class~=xzh][specValueId='+specValueId+']').find('input[name=spec_rel_images]').val();
    //'0|/sxxx.png|uri^3|dfd.png|uri'
    var arrImages = [];
    if (typeof (existImages) != 'undefined') {
        var arrSingle = existImages.split('^');
        for (var i = 0; i < arrSingle.length; i++) {
            var temp = arrSingle[i].split('|');
            if (temp.length >= 3) {
                arrImages.push({id: temp[0], path: temp[1],src:temp[2]});
            }
        }
    }
    return arrImages;
};

//设置图片
specProductEditor.setExistGoodsImages = function (specValueId, result) {
    $('div[class~=xzh][specValueId='+specValueId+']').find('input[name=spec_rel_images]').val(result);
}

//获取已经录入过的规格，保证在组合规格时，这些已经录入过的数据不会被清除掉
specProductEditor.getRecordedRows = function () {
    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var rowObjs = {};

    recordedRows.each(function () {
        //var temp = [];
        if (!window.ActiveXObject) {
            $(this).find('input').each(function () {//chrome等浏览器下坑爹的innerHTML不会带上input等的值
                //temp.push(this.name + ":" + this.value);
                switch (this.type) {
                    case 'radio':
                    case 'checkbox':
                        if (this.checked) this.setAttribute('checked', true);
                        else this.removeAttribute('checked');
                        break;
                    default:
                        this.setAttribute('value', this.value);
                }
            });
        }
        rowObjs[this.getAttribute('ppid')] = $(this).html();
    });
    return rowObjs;
};

//更新货品列表
specProductEditor.updateProductTable = function () {
    //获取已经存在的行
    var _recordedRows = specProductEditor.getRecordedRows();
    goodsEditor.showLoadding();


    //读取模板
    var _tmplProductTableRow = $('#tmplProductTableRow').html();

    //表格行
    var _rowsHtml = [];

    var arrSelectedSpecVals = this.selectedSpecValues;
    if (arrSelectedSpecVals.length > 0) {
        descarteHelper.init(arrSelectedSpecVals);
        descarteHelper.Process(0);
        var result = descarteHelper.getResult();
        if(result.length > 0){
            $("#batch-update").show();
        }else{
            $("#batch-update").hide();
        }
        $('#specProductList').html('');

        for (var i = 0; i < result.length; i++) {//所有规格的组合
            var specValTds = '';
            var rowIds = [];
            for (var k = 0; k < result[i].length; k++) {
                var objSpecVal = result[i][k];
                var specValueImg = specProductEditor.getExistGoodsImages(objSpecVal.specValueId);
                var isImg = false;
                if(specValueImg != null && specValueImg.length > 0){
                    isImg = true;
                }
                //#region 规格别名处理 1013
                var customName = $('div[class~=xzh][specValueId='+objSpecVal.specValueId+']').find('input[name=spec_value_customer]').val();
                if (customName == '') {
                    customName = $('div[class~=xzh][specValueId='+objSpecVal.specValueId+']').find('label[class~=js-spec_value_origin]').text();
                }
                _spec_Value = customName;
                //#endregion

                //{0}为规格值ID，{1}为规格值名称（别名）,{2}位规格ID
                specValTds += hot.utils.formatString('<td width="10%">'
                    + '<input type="hidden" name="pro_spec_value_id_{2}" value="{0}" />'
                    + '<input type="hidden" name="pro_spec_value_{2}" class="cls_spec_value_{0}" value="{1}" />'
                    + '<span style="color:#f96;" name="txtpro_spec_value_{0}">{1}</span>'
                    + '<img name="pro_spec_value_img_{0}" src="{3}" width="50" height="50">'
                    + '</td>',
                    objSpecVal.specValueId,
                    _spec_Value,
                    objSpecVal.specId,
                    isImg?specValueImg[0].src:'/resource/images/dg2017.jpg');

                rowIds.push(objSpecVal.specId + '|' + objSpecVal.specValueId);
            }
            var clsname = i % 2 != 0 ? "even" : "";
            var ppid = rowIds.join('_');

            var tempRow = _tmplProductTableRow.replace("{$specs$}", specValTds);
            tempRow = tempRow.replace("{$rowclass$}", clsname);
            tempRow = tempRow.replace("{$specids}", ppid).replace("{$specids}",ppid);
            tempRow = tempRow.replace(/{specids}/g, ppid);
            //tempRow = tempRow.replace(/{$specids}/g, ppid);

            //修改模式下赋值，复选框取消后，恢复其在数据库中的数据
            //根据ppid，赋值
            for (var p in this.exsitProductRows) {
                if (p == ppid) {//使用修改前的行内容
                    tempRow = hot.utils.formatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
                        this.exsitProductRows[p],
                        clsname,
                        ppid);
                    break;
                }
            }

            //----------------------------------
            for (var p in _recordedRows) {
                if (p == ppid) {//使用原来的行内容
                    tempRow = hot.utils.formatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
                        _recordedRows[p],
                        clsname,
                        ppid);
                    break;
                }
            }
            //_rowsHtml.push(tempRow);
            $('#specProductList').append(tempRow);
        }
    }
    //设置默认的参数
    specProductEditor.setProductParamas();
}

specProductEditor.setProductParamas = function () {//设置货品的价格等各项参数
    // var inputIds = ['txtPrice', 'txtCost', 'txtMktprice', 'txtProductBN', 'txtWeight', 'txtStore'];
    var objExsitBNs = {};
    for (var p in this.exsitProductRows) {
        $(this.exsitProductRows[p]).find('input[name="pro_bn"]').each(function () {
            objExsitBNs[this.value] = true;
        });
    }

    $('input[name="pro_bn"]').each(function (index) {
        objExsitBNs[this.value] = true;
    });

    var arrExsitBNs = [];
    for (var p in objExsitBNs) {
        arrExsitBNs.push(p);
    }

    $('input[name="pro_bn"]').each(function (index) {
        if (this.value == '') {
            var bn = specProductEditor.sellInfo['txtProductBN'];
            if (bn != '') {
                //bn += '-' + (index + 1);//这里暂时使用排序号来自动生成
                bn = specProductEditor.buildProductBnByIndex(bn, index + 1, arrExsitBNs);
            }
            this.value = bn;
        }
    });
    _userPricesModule.verifyRebateAll();
    /*

     $('input[name="pro_store"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtStore'];
     }
     });

     $('input[name="pro_mktPrice"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtMktPrice'];
     }
     });

     $('input[name="pro_cost"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtCost'];
     }
     });

     $('input[name="pro_weight"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtWeight'];
     }
     });

     $('input[name="pro_min_price"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtPrice'];
     }
     });
     */

    /*$("input[name='lvPriceInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvPriceInfogood'];
        }
    })

    $("input[name='lvIntegralInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvIntegralInfogood'];
        }
    })*/
};
specProductEditor.buildProductBnByIndex = function (bn, index, exsitBns) {
    while (_checkBNExsit(bn, index, exsitBns)) {
        index++;
    }
    var newBN = bn + '-' + index;
    exsitBns.push(newBN);
    return newBN;
};

function _checkBNExsit(bn, index, exsitBns) {
    var _bn = bn + '-' + index;
    for (var i = 0; i < exsitBns.length; i++) {
        if (exsitBns[i] == _bn) {
            return true;
        }
    }
    return false;
}


//#货品相关
//批量填充
specProductEditor.setBatchProductParams = function(){ //批量设置货品参数
    var arrExsitBNs = [];
    $('input[name="pro_bn"]').each(function (index) {
        if ($.trim($("#batch-seller-id").val()).length > 0) {
            var bn = $.trim($("#batch-seller-id").val());
            if (bn != '') {
                //bn += '-' + (index + 1);//这里暂时使用排序号来自动生成
                bn = specProductEditor.buildProductBnByIndex(bn, index + 1, arrExsitBNs);
            }
            this.value = bn;
        }
    });
    $('input[name="pro_store"]').each(function (index) {
        if ($.trim($("#batch-store").val()) > 0) {
            this.value = $.trim($("#batch-store").val());
        }
    });

    $('input[name="pro_mktPrice"]').each(function (index) {
        if ($.trim($("#batch-mktPrice").val()) > 0) {
            this.value = $.trim($("#batch-mktPrice").val());
        }
    });

    $('input[name="pro_cost"]').each(function (index) {
        if ($.trim($("#batch-cost").val()) > 0) {
            this.value = $.trim($("#batch-cost").val());
        }
    });

    $('input[name="pro_weight"]').each(function (index) {
        if ($.trim($("#batch-weight").val()) > 0) {
            this.value = $.trim($("#batch-weight").val());
        }
    });

    $('input[name="pro_min_price"]').each(function (index) {
        if ($.trim($("#batch-min-price").val()) > 0) {
            this.value = $.trim($("#batch-min-price").val());
        }
    });
    /*$('input[name="pro_max_price"]').each(function (index) {
        if ($.trim($("#batch-max-price").val()) > 0) {
            this.value = $.trim($("#batch-max-price").val());
        }
    });*/

    var batchUserPrice = $.trim($("#js-batch-userPrice").val());
    if (batchUserPrice.length > 0)
        specProductEditor.setUserPrices(batchUserPrice);
    var batchUserCashIntegral = $.trim($("#js-batch-userCashIntegral").val());
    if(batchUserCashIntegral.length > 0){
        specProductEditor.setUserCashIntegral(batchUserCashIntegral);
    }
    if(settlementMode){
        var batchRebateType = $.trim($("#js-batch-user-rebate-type").val());
        if (batchRebateType.length > 0 && rebateMode == true){
            $(".js-batch-user-rebate-percent-value-type").val(batchRebateType);
        }else if(batchRebateType.length > 0 && rebateMode == false){
            $(".js-batch-user-rebate-value-type").val(batchRebateType);
        }
        _userPricesModule.verifyRebateAll();
    }
}

//组合货品数据，用于提交后台
specProductEditor.getRecordList = function(){
    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var propList = [];
    var inputIds = ['supplierProductId','minPrice','maxPrice', 'cost', 'mktPrice', 'bn', 'weight', 'store','pdtDesc','props','userPriceInfo','userIntegralInfo','minUserPrice','disRebateCustomPercent'];
    var specProps = ['SpecId','SpecValueId','SpecValue'];
    recordedRows.each(function(){
        var prop = {};
        prop[inputIds[0]] = $(this).find('input[name="pro_id"]').val();
        prop[inputIds[1]] = $(this).find('input[name="pro_min_price"]').val();
        prop[inputIds[2]] = $(this).find('input[name="pro_min_price"]').val();
        // prop[inputIds[2]] = $(this).find('input[name="pro_max_price"]').val();
        prop[inputIds[3]] = $(this).find('input[name="pro_cost"]').val();
        // prop[inputIds[4]] = $(this).find('input[name="pro_mktPrice"]').val();
        prop[inputIds[5]] = $(this).find('input[name="pro_bn"]').val();
        prop[inputIds[6]] = $(this).find('input[name="pro_weight"]').val();
        prop[inputIds[7]] = $(this).find('input[name="pro_store"]').val();
        var pdtDest = "";
        var specPropList = [];
        var ppid = $(this).find('input[name="ppid"]').val();
        var ppidList = ppid.split("_");
        $(this).find('span[name*=txtpro_spec_value_]').each(function(i){
            if(pdtDest.length != 0){
                pdtDest += ",";
            }
            pdtDest += this.textContent;
            var specProp = {};
            specProp[specProps[0]] = ppidList[i].split("|")[0];
            specProp[specProps[1]] = ppidList[i].split("|")[1];
            specProp[specProps[2]] = this.textContent;
            specPropList.push(specProp);
        })
        prop[inputIds[8]] = pdtDest;
        prop[inputIds[9]] = JSON.stringify(specPropList);
        prop[inputIds[10]] = $(this).find('input[name="lvPriceInfo"]').val();
        prop[inputIds[11]] = $(this).find('input[name="lvIntegralInfo"]').val();
        var pro_id = $(this).find('input[name="pro_id"]').val();
        var minUserPrice ;
        if(pro_id != '0'){
            minUserPrice= _userPricesModule.getMinUserPrices(pro_id);
        }else{
            minUserPrice = _userPricesModule.getMinUserPrices(ppid);
        }
        prop[inputIds[12]] = minUserPrice;
        if(settlementMode){
            prop[inputIds[13]] = $(this).find('input[name="pro_disrebate_pecent_unified"]').val();
        }
        if(prop[inputIds[13]] == 'undefined' && prop[inputIds[13]].length == 0){
            prop[inputIds[13]] = "-1";
        }
        propList.push(prop);
    });
    return JSON.stringify(propList);
}

specProductEditor.getSpecDescList = function(){ //组合选中规格数据，用于提交后台
    var inputIds = ['SpecId','SpecValue','SpecValueId','SpecImage','GoodsImageIds'];
    var specDescList = [];
    for (var i = 0; i < this.currentSpecList.length; i++) {
        $('input[class~=js-specValue-check][specId=' + this.currentSpecList[i].specId + ']:checked').each(function () {
            var specDesc = {};
            specDesc[inputIds[0]] = $(this).attr('specId');
            var specValue = parseInt($(this).attr('specValue'));
            var customName = $(this).siblings('input[name=spec_value_customer]').val();
            specDesc[inputIds[1]] = customName.length>0?customName:specValue;
            specDesc[inputIds[2]] = parseInt($(this).attr('specValueId'));
            specDesc[inputIds[3]] = '';
            var imgs = $(this).siblings("input[name=spec_rel_images]").val();
            var imagePaths = [];
            if(imgs.length > 0){
                var arrSingle = imgs.split('^');
                for(var j = 0 ; j < arrSingle.length ; j++){
                    var temp = arrSingle[j].split('|');
                    if(temp.length >= 2){
                        imagePaths.push(temp[1]);
                    }
                }
            }
            specDesc[inputIds[4]] =imagePaths;
            specDescList.push(specDesc);
        });
    }
    return JSON.stringify(specDescList);
}

specProductEditor.checkForm = function () {//提交前的检查表单
    //#region 商家编码检查
    var objResult = { flag: false, message: '' };

    if ($('#hidOpenCustomSpec').val() == '0') {
        objResult.flag = true;
        return objResult;
    }

    var arr_pro_bn = [];
    $('input[name="pro_bn"]').each(function () {
        arr_pro_bn.push(this.value);
    });
    if (arr_pro_bn.length == 0) {
        objResult.message = '货品未录入';
        return objResult;
    }

    for (var i = 0; i < arr_pro_bn.length; i++) {
        if (arr_pro_bn == '') {
            objResult.message = '商家编码不能为空，请检查';
            return objResult;
        }
    }

    var hash = {};
    for (var i = 0; i < arr_pro_bn.length; i++) {
        if (hash[arr_pro_bn[i]]) {
            objResult.message = '存在相同的商家编码"' + arr_pro_bn[i] + '"，请检查';
            return objResult;
        } else {
            hash[arr_pro_bn[i]] = true;
        }
    }

    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var flag = 1;
    recordedRows.each(function(){
        var freez = $(this).find('span[name="pro_freez"]').text();
        var store = $(this).find('input[name="pro_store"]').val();
        var pro_bn = $(this).find('input[name="pro_bn"]').val();
        if(Number(store) < Number(freez)){
            objResult.message = '编码为"' + pro_bn + '"的货品 库存设置必须大于预占库存，请检查';
            flag=0;
            return;
        }
    });
    if(flag == 0){
        return objResult;
    }
    //#endregion

    objResult.flag = true;
    return objResult;
};
var _userPricesModule = {
    getUserPricesModel: function (priceStr) {
        if (priceStr == '') {
            return null;
        }
        var array = priceStr.split('|');
        var _data = [];
        for (var i = 0; i < array.length; i++) {
            var _arrayList = array[i].split(':');
            var levelId = _arrayList[0];
            var _price = _arrayList[1];
            var _maxIntegal = _arrayList[2];
            _data.push({ levelId: levelId, userPrice: _price, maxIntegal: _maxIntegal });
        }
        return _data;
    },
    setUserPrices: function (prices, data) {
        for (var i = 0; i < data.length; i++) {
            data[i].userPrice = prices;
        }
        return data;
    },
    setUserCashIntegral: function (integral, data) {
        for (var i = 0; i < data.length; i++) {
            data[i].maxIntegal = integral;
        }
        return data;
    },
    getUserPricesStr: function (data) {
        var _pricesStr = "";
        for (var i = 0; i < data.length; i++) {
            _pricesStr += data[i].levelId + ":" + data[i].userPrice + ":" + data[i].maxIntegal + "|";
        }
        return _pricesStr.substr(0, _pricesStr.length - 1);
    },
    getUserPricesModelByNull:function(prices) {
        var _levelIds = $("#hdLevelList").val();
        var _array = _levelIds.split(',');
        var _data = [];
        $.each(_array, function (item, dom) {
            var _value = dom;
            _data.push({ levelId: _value, userPrice: prices, maxIntegal: 0 });
        })
        return _data;
    },
    getMinUserPrices: function (index) {
        var _value = document.getElementById("lvPriceInfo" + index).value;
        if (_value == '' || _value == 'undefined') {
            return document.getElementById("pro_min_price" + index).value
        }
        var _defaultValue = document.getElementById("pro_min_price" + index).value;
        var _data = _userPricesModule.getUserPricesModel(_value);
        var _list = [];
        for (var i = 0; i < _data.length; i++) {
            if (_data[i].userPrice != '' && _data[i].userPrice != "0" && _data[i].userPrice != "-1") {
                _list.push(_data[i].userPrice)
            }else{
                _list.push(_defaultValue);
            }
        }
        if (_list.length > 0) {
            return Math.min.apply(null, _list);
        } else {
            return document.getElementById("pro_min_price" + index).value
            //return $("#pro_min_price" + index).val();
        }
        return 0;
    },
    getMaxUserPrices:function(index){
        var _value = document.getElementById("lvPriceInfo" + index).value;
        if (_value == '' || _value == 'undefined') {
            return document.getElementById("pro_min_price" + index).value
        }
        var _defaultValue = document.getElementById("pro_min_price" + index).value;
        var _data = _userPricesModule.getUserPricesModel(_value);
        var _list = [];
        for (var i = 0; i < _data.length; i++) {
            if (_data[i].userPrice != '' && _data[i].userPrice != "0" && _data[i].userPrice != "-1") {
                _list.push(_data[i].userPrice)
            }else{
                _list.push(_defaultValue);
            }
        }
        if (_list.length > 0) {
            return Math.max.apply(null, _list);
        } else {
            return document.getElementById("pro_min_price" + index).value
            //return $("#pro_min_price" + index).val();
        }
        return 0;
    },
    calculatePercent: function (prices, minPrices) {
        return (prices / minPrices * 100);
    },
    calculatePrice: function(percent,minPrices){
        return (percent * minPrices / 100).toFixed(2);
    },
    verifyRebatePercent: function (_index) {
        if (document.getElementById("js-batch-user-rebate-value-type-" + _index)) {
            var _rebate = document.getElementById("js-batch-user-rebate-value-type-" + _index).value;
            var minPrices = _userPricesModule.getMinUserPrices(_index);
            // var maxPrices = _userPricesModule.getMaxUserPrices(_index);
            // var _rebate_percent_min = _userPricesModule.calculatePercent(_rebate, maxPrices);
            var _rebate_percent_max = _userPricesModule.calculatePercent(_rebate,minPrices);
            document.getElementById("js-batch-user-rebate-percent-value-type-" + _index).value = _rebate_percent_max;
        }
    },
    verifyRebate: function (_index) {
        if(document.getElementById("js-batch-user-rebate-percent-value-type-" + _index)){
            var minPrices = _userPricesModule.getMinUserPrices(_index);
            // var maxPrices = _userPricesModule.getMaxUserPrices(_index);
            var _rebate_percent = document.getElementById("js-batch-user-rebate-percent-value-type-" + _index).value;
            if(_rebate_percent == '' || _rebate_percent == 'undefined'){
                _rebate_percent = document.getElementById("disRebatePercent").value;
            }
            var _min_rebate = _userPricesModule.calculatePrice(_rebate_percent,minPrices);
            // var _max_rebate = _userPricesModule.calculatePrice(_rebate_percent,maxPrices);
            document.getElementById("js-batch-user-rebate-value-type-" + _index).value = _min_rebate;
        }
    },
    verifyRebateAll: function () {
        var obj = $(".js-goods-price-info");
        var _index = 0;
        $.each(obj, function (item, dom) {
            var _productId = $(dom).attr("data-productid");
            _userPricesModule.verifyRebate(_productId);
            if (document.getElementById("js-batch-user-rebate-value-type-" + _productId)) {
                var classList = document.getElementById("js-batch-user-rebate-value-type-" + _productId).className;
                if (classList.indexOf("disabled") > 0) {
                    _index++;
                }
            }
        })
        return _index <= 0;
    },
    bindVerifyRebate: function () {
        var obj = $(".js-price-rebate-verity");
        obj.unbind("blur");
        $.each(obj, function (item, dom) {
            $(dom).blur(function () {
                if (!_userPricesModule.verifyRebateAll()) {
                    hot.tip.msg("返利金额超出预警！");
                    return false;
                }
            })
        })
    }
}
specProductEditor.setUserPrices = function (prices) {
    var obj = $(".js-goods-price-info");
    $.each(obj, function (item, dom) {
        var _value = $(dom).val();
        var _index = $(dom).attr("data-productid");
        //var minPrices = _userPricesModule.getMinUserPrices(_index);
        //1929:120:0|2015:130:0|4331:140:0|1377:150:0
        if (_value == "" || _value == 'undefined') {//如果木有设置则获得会员等级信息并构建所需要的数据值
            var _data = _userPricesModule.getUserPricesModelByNull(prices);
            _value = _userPricesModule.getUserPricesStr(_data);
            $(dom).val(_value)
        } else {
            var _data = _userPricesModule.getUserPricesModel(_value);
            _data = _userPricesModule.setUserPrices(prices, _data);
            _value = _userPricesModule.getUserPricesStr(_data);
            $(dom).val(_value)
        }
        _userPricesModule.verifyRebate(_index);
    })

}

specProductEditor.setUserCashIntegral = function (cashIntegral) {
    var obj = $(".js-goods-price-info");
    $.each(obj, function (item, dom) {
        var _value = $(dom).val();
        //1929:120:0|2015:130:0|4331:140:0|1377:150:0
        if (_value == "") {//如果木有设置则获得会员等级信息并构建所需要的数据值
            var _data = _userPricesModule.getUserPricesModelByNull({price:undefined,integral:cashIntegral});
            _value = _userPricesModule.getUserPricesStr(_data);
            $(dom).val(_value);
        } else {
            var _data = _userPricesModule.getUserPricesModel(_value);
            _data = _userPricesModule.setUserCashIntegral(cashIntegral, _data);
            _value = _userPricesModule.getUserPricesStr(_data);
            $(dom).val(_value);
        }
    })

}

//#region 笛卡尔积助手
var descarteHelper = {};
descarteHelper.result = [];
descarteHelper.zz = [];

descarteHelper.init = function (arrDatas) {
    this.result = [];
    this.zz = arrDatas;
};

descarteHelper.getResult = function () {
    return this.result;
};

descarteHelper.Process = function (arrIndex, aresult) {
    if (arrIndex >= this.zz.length) { this.result.push(aresult); return; };
    var aArr = this.zz[arrIndex];
    if (!aresult) aresult = new Array();
    for (var i = 0; i < aArr.length; i++) {
        var theResult = aresult.slice(0, aresult.length);
        theResult.push(aArr[i]);
        descarteHelper.Process(arrIndex + 1, theResult);
    }
};
//#endregion

//#region 其他
var goodsEditor = {};
//加载loadding
goodsEditor.showLoadding = function (note, autoHide) {
    if (typeof (note) == 'undefined') {
        note = '正在处理';
    }
    $('.swt-messageBox-waittingText').html(note);
    $('#swt-messageBox-overlay').show();
    $('#swt-messageBox-waiting').show();
    setTimeout(function () {
        if (typeof (autoHide) == 'undefined' || autoHide == true) {
            goodsEditor.hideLoadding();
        }
    }, 600);
};

//隐藏loadding
goodsEditor.hideLoadding = function () {
    $('#swt-messageBox-overlay').hide();
    $('#swt-messageBox-waiting').hide();
};
//#endregion
//会员价积分相关
var userInfoHandler = {
    userPriceModal: $("#level_price_dialog").modal("设置会员等级价格与积分抵用上限", function (index, layerro) {
        var ppid = $("#level_price_ppid").val();
        var priceInfoDom = document.getElementById("lvPriceInfo" + ppid);
        var levelListStr = $("#hdLevelList").val();
        var levelList = levelListStr.split(',');
        var priceInfoStr = "";
        $.each(levelList, function (o, item) {
            var price = $("#price" + item).val();
            var maxIntegral = $("#maxIntegral" + item).val();
            if (price == "" || price == null) {
                price = -1;
            }
            if (maxIntegral == "" || maxIntegral == null) {
                maxIntegral = 0;
            }
            if (o == levelList.length - 1)
                priceInfoStr += item + ":" + price + ":" + maxIntegral;
            else
                priceInfoStr += item + ":" + price + ":" + maxIntegral + "|";
        });
        priceInfoDom.value = priceInfoStr;
        if(settlementMode){
            if(rebateMode){
                _userPricesModule.verifyRebate(ppid);
            }else{
                _userPricesModule.verifyRebatePercent(ppid);
            }
        }
        userInfoHandler.userPriceModal.hide();
    }),
    userPriceConfig: function (ppid) {
        this.userPriceModal.show(function () {
            userInfoHandler.setUserPrice(ppid);
            $("#level_price_ppid").val(ppid);
        });
    },
    setUserPrice: function (ppid) {
        var priceInfo = document.getElementById("lvPriceInfo" + ppid).value; //$("#lvPriceInfo" + ppid).val() //levelid:price|levelid:price
        if (priceInfo != null && priceInfo != "") {
            var levelPriceList = priceInfo.split('|');
            $.each(levelPriceList, function (o, item) {
                var tempPrice = item.split(':');
                if (tempPrice[1] > 0) {
                    $("#price" + tempPrice[0]).val(tempPrice[1]);
                }
                if (tempPrice[2] >= 0) {
                    $("#maxIntegral" + tempPrice[0]).val(tempPrice[2]);
                }
            });
        }
    },
    userIntegralModal: $("#level_integral_dialog").modal("设置会员等级积分", function (index, layerro) {
        var integralInfoDom = document.getElementById("lvIntegralInfo" + $("#level_integral_ppid").val());
        var levelListStr = $("#hdLevelList").val();
        var levelList = levelListStr.split(',');
        var integralInfoStr = "";
        $.each(levelList, function (o, item) {
            var integral = $("#integral" + item).val();
            var underIntegral = $("#underIntegral" + item).val();

            if (integral == "" || integral == null) {
                integral = 0;
            }
            if (underIntegral == "" || underIntegral == null) {
                underIntegral = 0;
            }

            if (o == levelList.length - 1)
                integralInfoStr += item + ":" + integral + ":" + underIntegral;
            else
                integralInfoStr += item + ":" + integral + ":" + underIntegral + "|";
        });
        integralInfoDom.value = integralInfoStr;
        userInfoHandler.userIntegralModal.hide();
    }),
    userIntegralConfig: function (ppid) {
        this.userIntegralModal.show(function () {
            userInfoHandler.setUserIntegral(ppid);
            $("#level_integral_ppid").val(ppid);
        });
    },
    setUserIntegral: function (ppid) {
        var integralInfo = document.getElementById("lvIntegralInfo" + ppid).value;
        if (integralInfo != null && integralInfo != "") {
            var levelIntegralList = integralInfo.split('|');
            $.each(levelIntegralList, function (o, item) {
                var tempIntegral = item.split(':');
                if (tempIntegral[1] > 0) {
                    $("#integral" + tempIntegral[0]).val(tempIntegral[1]);
                }
                if (tempIntegral[2] > 0) {
                    $("#underIntegral" + tempIntegral[0]).val(tempIntegral[2]);
                }
            })
        }
    },
    batchSetInfo: function (obj) {
        var value = obj.value;
        var toSet = $(obj).attr("to-set");
        $("input[data-name=" + toSet + "]").val(value);
    }
}