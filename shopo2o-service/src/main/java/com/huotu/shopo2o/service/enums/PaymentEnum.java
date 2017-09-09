package com.huotu.shopo2o.service.enums;

import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * Created by hxh on 2017-09-08.
 */
public interface PaymentEnum {
    /**
     * 支付状态
     */
    enum PayStatus implements ICommonEnum {
        SUCCESS(1, "支付成功"),
        FAILED(2, "支付失败"),
        CANCEL(3, "未支付"),
        ERROR(4, "处理异常"),
        INVALID(5, "非法参数"),
        PROGRESS(6, "处理中"),
        TIMEOUT(7,"超时"),
        READY(8,"准备中");

        private Integer code;
        private String value;

        PayStatus(Integer code, String value) {
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
