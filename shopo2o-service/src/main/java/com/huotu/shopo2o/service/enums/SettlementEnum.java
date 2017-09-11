package com.huotu.shopo2o.service.enums;

import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * Created by hxh on 2017-09-11.
 */
public interface SettlementEnum {
    //结算单状态
    enum SettlementStatusEnum implements ICommonEnum {
        CHECKING(0, "核实中"),
        READY_SETTLE(1, "待结算"),
        SETTLED(2, "已结算"),
        NO_SETTLED(3,"不参与结算");

        private Integer code;
        private String value;

        SettlementStatusEnum(Integer code, String value) {
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

    //结算单审核状态
    enum SettlementCheckStatus implements ICommonEnum {
        NOT_CHECK(0, "待审核"),
        CHECKED(1, "审核通过"),
        RETURNED(2, "审核不通过");

        private Integer code;
        private String value;

        SettlementCheckStatus(Integer code, String value) {
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
