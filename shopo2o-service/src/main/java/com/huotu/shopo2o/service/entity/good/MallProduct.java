package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hxh on 2017-09-07.
 */
@Entity
@Table(name = "Mall_Products")
@Setter
@Getter
@Cacheable(false)
public class MallProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Id")
    private Integer productId;

    @Column(name = "Barcode")
    private String barcode;

    @Column(name = "Title")
    private String title;

    @Column(name = "Bn")
    private String bn;


    @ManyToOne
    @JoinColumn(name = "Goods_Id")
    private MallGood good;

    /**
     * 规格
     */
    @Column(name = "Pdt_Desc")
    private String standard;

    /**
     * 成本价
     */
    @Column(name = "Cost")
    private double cost;
    /**
     * 销售价
     */
    @Column(name = "Price")
    private double price;
    @Column(name = "Mktprice")
    private double mktPrice;
    @Column(name = "Name")
    private String name;
    @Column(name = "Weight")
    private double weight;
    @Column(name = "Unit")
    private String unit;
    @Column(name = "Store")
    private int store;
    @Column(name = "Freez")
    private int freez;
    @Column(name = "Props")
    private String props;
    @Column(name = "Marketable")
    private int marketable;
    @Column(name = "Supplier_Product_Id")
    private int supplierProductId;
    @Column(name = "Last_Modify")
    private Date lastModify;
    @Column(name = "UserPriceInfo")
    private String userPriceInfo;
    @Column(name = "UserIntegralInfo")
    private String userIntegralInfo;
    @Column(name = "DisRebateCustomPercent")
    private Double disRebateCustomPercent;

    // TODO: 3/17/16
}
