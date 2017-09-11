package com.huotu.shopo2o.service.entity.order;

import com.huotu.shopo2o.service.enums.PaymentEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Payments")
@Cacheable
@Getter
@Setter
public class MallPayments {
    @Id
    @Column(name = "Payment_Id")
    private String paymentId;

    @Column(name = "Order_Id")
    private String orderId;

    /**
     * 单据日期
     */
    @Column(name = "T_Begin")
    private Date begin;
    /**
     * 支付金额
     */
    @Column(name = "Money")
    private double money;
    /**
     * 支付方式
     */
    @Column(name = "Paymethod")
    private String payMethod;
    /**
     * 状态
     */
    @Column(name = "Status")
    private PaymentEnum.PayStatus status;

}
