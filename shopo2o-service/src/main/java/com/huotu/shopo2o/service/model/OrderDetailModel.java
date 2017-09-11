package com.huotu.shopo2o.service.model;

import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallOrderItem;
import com.huotu.shopo2o.service.entity.order.MallPayments;
import com.huotu.shopo2o.service.entity.order.MallRefunds;
import com.huotu.shopo2o.service.enums.OrderEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
@Getter
@Setter
public class OrderDetailModel {
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 订单状态
     */
    private OrderEnum.OrderStatus orderStatus;
    /**
     * 支付状态
     */
    private OrderEnum.PayStatus payStatus;
    /**
     * 发货状态
     */
    private OrderEnum.ShipStatus shipStatus;
    /**
     * 能否发货
     */
    private boolean deliveryable;
    /**
     * 订单来源
     */
    private OrderEnum.OrderSourceType orderSourceType;
    /**
     * 发货单列表
     */
    private List<MallDelivery> deliveryList;
    /**
     * 退货单列表
     */
    private List<MallDelivery> refundsList;
    /**
     * 付款列表
     */
    private List<MallPayments> paymentsList;
    /**
     * 退款列表
     */
    private List<MallRefunds> refundsMoneyList;
    /**
     * 收货人
     */
    private String shipName;
    /**
     * 身份证
     */
    private String identityCard;

    /**
     * 收货人电话
     */
    private String shipTel;

    /**
     * 收货人手机号
     */
    private String shipMobile;

    /**
     * 收货人地区
     */
    private String shipArea;

    /**
     * 收货人地址
     */
    private String shipAddr;

    /**
     * 实付金额（含运费）
     */
    private double finalAmount;

    /**
     * 运费
     */
    private double costFreight;

    /**
     * 成本价
     */
    private double costPrice;
    /**
     * 返利金额
     */
    private double disRebate;
    // TODO: 2017-09-11  结算方式：0->成本价；1->提点  (MallCustomer中去掉了结算和预结算状态，这个计算与这两个状态有关)
//    private int settleMode;
    /**
     * 平台提点
     */
    private double commission;

    /**
     * 下单时间
     */
    private String createTime;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 商品列表
     */
    private List<MallOrderItem> supOrderItemList = new ArrayList<>();
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 用户附言
     */
    private String memo;
    /**
     * 是否需要开票
     */
    private int isTax;
    /**
     * 开票公司抬头
     */
    private String taxCompany;
}
