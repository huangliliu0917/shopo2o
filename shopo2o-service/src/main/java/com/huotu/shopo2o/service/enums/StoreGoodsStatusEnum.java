package com.huotu.shopo2o.service.enums;

import com.huotu.shopo2o.common.ienum.ICommonEnum;

/**
 * Created by hxh on 2017-09-18.
 */
public interface StoreGoodsStatusEnum {
    /**
     * 货品审核状态
     */
    enum CheckStatusEnum implements ICommonEnum {
        UNSUBMIT(0, "未提交审核"),
        CHECKING(1, "审核中"),
        CHECKED(2, "已审核"),
        CHECK_FAILED(3, "审核失败"),
        RECYCLING(4, "回收审核中"),
        RECYCLED(5, "已回收"),
        RECYCLE_FAILED(6, "回收审核失败");

        private Integer code;
        private String value;

        CheckStatusEnum(Integer code, String value) {
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
