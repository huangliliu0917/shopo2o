package com.huotu.shopo2o.service.enums;

import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * 权限
 * Created by hxh on 2017-09-11.
 */
// TODO: 2017-09-11 不清楚哪些权限需要
public enum Authority implements ICommonEnum {
    MANAGER_ROOT("ROLE_ROOT", "超级管理员"),
    SUPPLIER_ROOT("ROLE_SUPPLIER", "供应商"),
    SUPPLIER_ORDER("ROLE_ORDER", "订单管理"),
    SUPPLIER_SHOP("ROLE_SHOP", "店铺管理"),
    SUPPLIER_SETTLEMENT("ROLE_SETTLEMENT", "结算管理"),
    SUPPLIER_GOOD("ROLE_GOOD", "商品管理");
    private String code;
    private String value;

    Authority(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
