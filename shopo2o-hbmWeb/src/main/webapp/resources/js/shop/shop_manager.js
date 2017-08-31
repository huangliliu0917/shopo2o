/**
 * Created by admin on 2017-08-22.
 */
var shopListHandler = {
    edit: function (obj) {
        var shopId = $(obj).attr("shopId");
        window.location.href = baseUrl + "edit?shopId=" + shopId;
    },
    changeOption: function (obj) {
        var shopId = $(obj).attr("shopId");
    },
    delete: function (obj) {
        var shopId = $(obj).attr("shopId");
    },
    addShop:function(){
        window.location.href= baseUrl + "edit";
    }
}