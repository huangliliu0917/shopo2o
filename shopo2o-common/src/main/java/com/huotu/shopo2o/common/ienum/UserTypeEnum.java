package com.huotu.shopo2o.common.ienum;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
public enum UserTypeEnum implements ICommonEnum {

    normal(0,"会员"),
    /**
     * 小伙伴
     */
    buddy(1,"小伙伴"),
    /**
     * 默认小伙伴
     */
    defaultBuddy(2,"其他");

    private Integer code;
    private String value;

    UserTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
