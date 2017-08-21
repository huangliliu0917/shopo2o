package com.huotu.shopo2o.service.enums;


import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * 供应商权限
 * Created by allan on 3/22/16.
 */
public enum AuthorityEnum implements ICommonEnum {
    MANAGER_ROOT("ROLE_ROOT", "超级管理员"),
    SHOP_ORDER("ROLE_ORDER", "订单管理"),
    SHOP_SHOP("ROLE_SHOP", "店铺管理");
    private String code;
    private String value;

    AuthorityEnum(String code, String value) {
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
