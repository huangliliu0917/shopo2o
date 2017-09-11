package com.huotu.shopo2o.service.enums;

import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * Created by hxh on 2017-09-11.
 */
public interface ActEnum {
    enum OrderPintuanStatusOption implements ICommonEnum {
        WAITING(0, "待开团"),
        GROUPING(1, "组团中"),
        GROUPED(2, "已成团"),
        FAILED(3, "成团失败");
        private Integer code;
        private String value;

        OrderPintuanStatusOption(Integer code, String value) {
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
}
