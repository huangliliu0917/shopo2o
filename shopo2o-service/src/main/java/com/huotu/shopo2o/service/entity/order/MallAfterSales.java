package com.huotu.shopo2o.service.entity.order;


import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.enums.OrderEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * 售后表
 * Created by hxh on 2017-09-07.
 */
@Entity
@Table(name = "Mall_AfterSales")
@Setter
@Getter
@Cacheable(value = false)
public class MallAfterSales {
    @Id
    @Column(name = "After_Id")
    private String afterId;
    @Column(name = "Member_Id")
    private int memberId;
    @Column(name = "Customer_Id")
    private int customerId;
    @Column(name = "Goods_Id")
    private int goodId;
    @Column(name = "Product_Id")
    private int productId;
    @OneToOne
    @JoinColumn(name = "Item_Id")
    private MallOrderItem orderItem;
    @Column(name = "Product_Name")
    private String productName;
    @Column(name = "Product_Num")
    private int productNum;
    @Column(name = "Integral")
    private int integral;
    @Column(name = "Bn")
    private String bn;
    @Column(name = "After_Status")
    private AfterSaleEnum.AfterSaleStatus afterSaleStatus;
    @Column(name = "Order_Id")
    private String orderId;
    @Column(name = "IntegralAmount")
    private double integralAmount;
    @Column(name = "AfterMoney")
    private double afterMoney;
    @Column(name = "AfterTime")
    private Date createTime;
    @Column(name = "Product_Img")
    private String productImg;
    @Transient
    private URI imgUri;

    @Column(name = "Pay_Status")
    private OrderEnum.PayStatus payStatus;
    @Column(name = "Store_Id")
    private int storeId;
    @Column(name = "Apply_Type")
    private AfterSaleEnum.AfterSaleType afterSaleType;
    @Column(name = "Apply_Reason")
    private AfterSaleEnum.AfterSalesReason afterSalesReason;
    @OneToMany(mappedBy = "afterSales")
    @OrderBy(value = "itemId DESC ")
    private List<MallAfterSalesItem> afterSalesItems;
    /**
     * 合伙人金币
     */
    @Column(name = "CptGold")
    private double cptCold;
    @Column(name = "Apply_Mobile")
    private String applyMobile;

    /**
     * 下单时间
     * @return
     */
    @Column(name = "OrderCreateTime")
    private Date orderCreateTime;
    /**
     * 订单支付时间
     */
    @Column(name = "OrderPayTime")
    private Date orderPayTime;

    public boolean refundable() {
        if (afterSaleType == AfterSaleEnum.AfterSaleType.REFUND) {
            return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING;
        } else {
            return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM;
        }
    }

    public boolean returnable() {
        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING &&
                afterSaleType == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND;
    }

    public boolean refusable() {
//        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING ||
//                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_BUYER_RETURN;
        //伙伴商城要求放开拒绝条件
        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.REFUNDING ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_BUYER_RETURN ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM;
    }
}
