package com.huotu.shopo2o.service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by hxh on 2017-09-11.
 */
@Getter
@Setter
public class DeliveryInfo {
    private String orderId;
    private String logiName;
    private String logiNo;
    private String logiCode;
    private String remark;
    private double freight;
    private String sendItems;
}
