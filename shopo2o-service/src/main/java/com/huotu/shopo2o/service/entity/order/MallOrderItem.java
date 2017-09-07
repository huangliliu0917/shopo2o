package com.huotu.shopo2o.service.entity.order;


import com.huotu.shopo2o.service.entity.good.MallProduct;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.enums.OrderEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.URI;

/**
 * Created by hxh on 2017-09-07.
 */
@Entity
@Table(name = "Mall_Order_Items")
@Cacheable(false)
@Setter
@Getter
public class MallOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_Id")
    private Long itemId;
    @ManyToOne
    @JoinColumn(name = "Order_Id")
    private MallOrder order;
    @ManyToOne
    @JoinColumn(name = "Product_Id", referencedColumnName = "Product_Id")
    private MallProduct product;
    @Column(name = "Bn")
    private String bn;
    @Column(name = "Name")
    private String name;
    @Column(name = "Cost")
    private double cost;
    @Column(name = "Price")
    private double price;
    @Column(name = "Amount")
    private double amount;
    @Column(name = "Nums")
    private int nums;
    @Column(name = "Sendnum")
    private int sendNum;
    @Column(name = "Supplier_Id")
    private long supplierId;
    @Column(name = "Customer_Id")
    private long customerId;
    @Column(name = "Goods_Id")
    private int goodId;
    @Column(name = "GoodBn")
    private String goodBn;
    @Column(name = "Ship_Status")
    private OrderEnum.ShipStatus shipStatus;
    @Lob
    @Column(name = "customFieldValues")
    private String customFieldValues;
    @OneToOne(mappedBy = "orderItem")
    private MallAfterSales afterSales;
    @Column(name = "Thumbnail_Pic")
    private String thumbnailPic;
    @Transient
    private URI picUri;
    @Column(name = "DisRebateAssigned")
    private Double disRebateAssigned;
    @Column(name = "CommissionAssigned")
    private Double commissionAssigned;

    public boolean returnable() {
        return afterSales != null && (afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.REFUNDING ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.REFUND_SUCCESS) &&
                shipStatus == OrderEnum.ShipStatus.DELIVERED &&
                afterSales.getAfterSaleType() == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND;
    }

    public boolean deliverable() {
        return (afterSales == null ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.CANCELED ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED) &&
                shipStatus == OrderEnum.ShipStatus.NOT_DELIVER;
    }

    /**
     * 是否是售后中
     *
     * @return
     */
    public boolean isInAfterSale() {
        return afterSales != null &&
                afterSales.getAfterSaleStatus() != AfterSaleEnum.AfterSaleStatus.CANCELED &&
                afterSales.getAfterSaleStatus() != AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED;
    }

    /**
     * 是否退款完成
     *
     * @return
     */
    public boolean isRefunded() {
        return afterSales != null &&
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.REFUND_SUCCESS;
    }
}
