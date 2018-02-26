package com.huotu.shopo2o.common.ienum;

/**
 * 返利计算模型
 * Created by luyuanyuan on 2017/10/11.
 */
public enum RebateCompatibleEnum implements ICommonEnum {
    threeMode(0, "三级金字塔"),
    oldMode(1, "老返利模式"),
    eightMode(2, "八级金字塔"),
    operator(3, "经营者模式");

    private Integer code;
    private String value;

    RebateCompatibleEnum(Integer code, String value) {
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
