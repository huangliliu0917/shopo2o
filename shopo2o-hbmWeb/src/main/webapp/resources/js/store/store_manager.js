/**
 * Created by admin on 2017-08-22.
 */
var shopListHandler = {
    edit: function (obj) {
        var storeId = $(obj).attr("storeId");
        window.location.href = baseUrl + "edit?storeId=" + storeId;
    },
    changeOption: function (obj) {
        var storeId = $(obj).attr("storeId");
        var data_disabled = $(obj).attr('data-disabled');
        hot.confirm('确定要切换门店状态吗',function(){
            hot.ajax(baseUrl + "changeOption",{
                storeId:storeId,
                isDisabled:!(data_disabled == 'true')
            },function(result){
                if(result.code == 200){
                    hot.tip.success("切换成功");
                    setTimeout(function(){
                        window.location.reload()
                    },300);
                }else{
                    hot.tip.error(result.data);
                }
            },function(){
                hot.tip.error("系统错误");
            },'post');
        })
    },
    delete: function (obj) {
        var storeId = $(obj).attr("storeId");
        hot.confirm("确定要删除该门店吗",function(){
            hot.ajax(baseUrl + "/remove",{
                storeId:storeId
            },function(result){
                if(result.code == 200){
                    hot.tip.success("删除成功");
                    setTimeout(function(){
                        window.location.reload()
                    },300);
                }else{
                    hot.tip.error(result.data);
                }
            },function(){
                hot.tip.error("系统错误");
            },'post');
        })
    },
    addShop:function(){
        window.location.href= baseUrl + "edit";
    }
}