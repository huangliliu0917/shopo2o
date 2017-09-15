package com.huotu.shopo2o.service.jsonformat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by hxh on 2017-09-15.
 */
@Getter
@Setter
public class OrderForDelivery {
    @JSONField(name = "OrderNo")
    private String orderNo;
    @JSONField(name = "LogiName")
    private String logiName;
    @JSONField(name = "LogiNo")
    private String logiNo;
    @JSONField(name = "LogiMoney")
    private double logiMoney;
    @JSONField(name = "LogiCode")
    private String logiCode;
    @JSONField(name = "Remark")
    private String remark;
}
