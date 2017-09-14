package com.huotu.shopo2o.service.enums;

/**
 * Created by allan on 3/23/16.
 */
public enum RoleType {
    STORE(0, "门店登录"),
    OPERATOR(1, "操作员登录");
    private int code;
    private String name;

    RoleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
