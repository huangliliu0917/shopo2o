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
};
specProductEditor.initNoSpecProduct = function(product){
    $('input[name="pro_bn"]').val(product[0].bn);
    $('input[name="pro_id"]').val(product[0].supplierProductId);
    $('input[name="pro_store"]').val(product[0].store);
    $('input[name="pro_mktPrice"]').val(product[0].mktPrice);
    $('input[name="pro_min_price"]').val(product[0].minPrice);
    $('input[name="pro_max_price"]').val(product[0].maxPrice);
    $('input[name="pro_cost"]').val(product[0].cost);
    $('input[name="pro_weight"]').val(product[0].weight);
}

//选中规格
specProductEditor.getCheckedSpecValue = function (obj) {
    //显示或隐藏规格编辑链接
    if (obj.checked == true) {
        $(obj).siblings('input[name=spec_value_customer]').removeClass('dl');
        $(obj).siblings('a[class~=js-spec-images-link]').removeClass('dl');
        $(obj).parents('div[class~=xzh]').addClass('on');
    } else {
        $(obj).siblings('input[name=spec_value_customer]').addClass('dl');
        $(obj).siblings('a[class~=js-spec-images-link]').addClass('dl');
        $(obj).parents('div[class~=xzh]').removeClass('on');
    }
    var arrTemp = [];
    //规格数量
    $("#divSpecList").children().each(function(i){
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

//点击上传图片
specProductEditor.relateSpecToImages = function (obj) {
    var specValueId = $(obj).attr('specValueId');
    var url = /*[[@{goods/specRelImages.html}]]*/'../goods/specRelImages.html'
    url = url + '?specvalueid=' + specValueId + '&rnd=' + Math.random();
    J.PopupIFrame(url, "定义规格图片", 800, 470, "product_menu22", {"确定": true}, "auto", "", function (result) {
    });
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

                //#region 规格别名处理 1013
                var customName = $('div[class~=xzh][specValueId='+objSpecVal.specValueId+']').find('input[name=spec_value_customer]').val();
                if (customName == '') {
                    customName = $('div[class~=xzh][specValueId='+objSpecVal.specValueId+']').find('label[class~=js-spec_value_origin]').text();
                }
                _spec_Value = customName;
                //#endregion

                //{0}为规格值ID，{1}为规格值名称（别名）,{2}位规格ID
                specValTds += J.FormatString('<td class="txt40 c">'
                    + '<input type="hidden" name="pro_spec_value_id_{2}" value="{0}" />'
                    + '<input type="hidden" name="pro_spec_value_{2}" class="cls_spec_value_{0}" value="{1}" />'
                    + '<span style="color:#f96;" name="txtpro_spec_value_{0}">{1}</span>'
                    + '</td>',
                    objSpecVal.specValueId,
                    _spec_Value,
                    objSpecVal.specId);

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
                    tempRow = J.FormatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
                        this.exsitProductRows[p],
                        clsname,
                        ppid);
                    break;
                }
            }

            //----------------------------------
            for (var p in _recordedRows) {
                if (p == ppid) {//使用原来的行内容
                    tempRow = J.FormatString('<tr class="{1}" ppid="{2}"  name="trProductInfo">{0}</tr>',
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

     $('input[name="pro_price"]').each(function (index) {
     if (this.value == '') {
     this.value = specProductEditor.sellInfo['txtPrice'];
     }
     });
     */

    $("input[name='lvPriceInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvPriceInfogood'];
        }
    })

    $("input[name='lvIntegralInfo']").each(function(index){
        if(this.value==''){
            this.value = specProductEditor.sellInfo['lvIntegralInfogood'];
        }
    })
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
        if ($.trim($("#batch-seller-id").val()) > 0) {
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
    $('input[name="pro_max_price"]').each(function (index) {
        if ($.trim($("#batch-max-price").val()) > 0) {
            this.value = $.trim($("#batch-max-price").val());
        }
    });
}

//组合货品数据，用于提交后台
specProductEditor.getRecordList = function(){
    var recordedRows = $('#specProductTable tr[name="trProductInfo"]');
    var propList = [];
    var inputIds = ['supplierProductId','minPrice','maxPrice', 'cost', 'mktPrice', 'bn', 'weight', 'store','pdtDesc','props'];
    var specProps = ['SpecId','SpecValueId','SpecValue'];
    recordedRows.each(function(){
        var prop = {};
        prop[inputIds[0]] = $(this).find('input[name="pro_id"]').val();
        prop[inputIds[1]] = $(this).find('input[name="pro_min_price"]').val();
        prop[inputIds[2]] = $(this).find('input[name="pro_max_price"]').val();
        prop[inputIds[3]] = $(this).find('input[name="pro_cost"]').val();
        prop[inputIds[4]] = $(this).find('input[name="pro_mktPrice"]').val();
        prop[inputIds[5]] = $(this).find('input[name="pro_bn"]').val();
        prop[inputIds[6]] = $(this).find('input[name="pro_weight"]').val();
        prop[inputIds[7]] = $(this).find('input[name="pro_store"]').val();
        var pdtDest = "";
        var specPropList = [];
        var ppid = $(this).find('input[name="ppid"]').val();
        var ppidList = ppid.split("_");
        $(this).find('span[id!="pro_freez"]').each(function(i){
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
        var freez = $(this).find('span[id="pro_freez"]').text();
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