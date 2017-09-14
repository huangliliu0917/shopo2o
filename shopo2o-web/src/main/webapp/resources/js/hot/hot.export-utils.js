/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 *
 */

/**
 * 导出相关脚本
 * Created by admin on 2017-04-18.
 */
var exportHelper = {
    checkPage : function (txtBeginPage, txtEndPage) {
        var numberBug = totalPage;//总页数
        if (txtBeginPage == '' || txtEndPage == '') {
            return {flag: false, msg: '请输入正确的起止页码'};
        }
        var beginPage = parseInt(txtBeginPage);
        var endPage = parseInt(txtEndPage);
        if (endPage < beginPage) {
            return {flag: false, msg: '结束页不能小于起始页'};
        }
        if (endPage > numberBug) {
            return {flag: false, msg: '结束页不能大于总页数'};
        }
        var _maxPage = 500;
        if (endPage - beginPage >= _maxPage) {
            $('#txtEndPage').focus().select();
            return {flag: false, msg: '最多导出' + _maxPage + '页'};
        }
        return {flag: true};
    }
}