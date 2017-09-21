package com.huotu.shopo2o.service.searchable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by hxh on 2017-09-07.
 */
@Getter
@Setter
public class OrderSearchCondition {
    private String orderId;
    private String shipName;
    private String shipMobile;
    private int orderStatus = -3;
    private int payStatus = -1;
    private int shipStatus = -1;
    private int paymentTypeStatus = -1;
    private int sourceTypeStatus = -1;
    private int settleStatus = -1;
    /**
     * 排序类型，1按支付时间，2按订单金额
     */
    private int orderType;
    /**
     * 排序规则,0表示DESC，1表示ASC
     */
    private int orderRule;
    private String beginTime;
    private String endTime;
    private String beginPayTime;
    private String endPayTime;
    /**
     * 供应商id
     */
    private long storeId;
    /**
     * 订单商品名称
     */
    private String orderItemName;
    /**
     * 分销商ID
     */
    private long customerId;
    /**
     * 供应商名称
     */
    private String storeName;
}
