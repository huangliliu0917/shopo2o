package com.huotu.shopo2o.service.entity.order;

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
@Table(name = "Mall_Refunds")
@Cacheable
@Getter
@Setter
public class MallRefunds {
    @Id
    @Column(name = "Refund_Id")
    private String refundId;

    @Column(name = "Order_Id")
    private String orderId;

    /**
     * 单据日期
     */
    @Column(name = "T_Ready")
    private Date readyTime;

    /**
     * 退款金额
     */
    @Column(name = "Money")
    private double money;

    /**
     * 退款方式
     */
    @Column(name = "Paymethod")
    private String payMethod;
}
