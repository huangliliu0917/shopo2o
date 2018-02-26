package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
@Entity
@Table(name = "Mall_Goods")
@Setter
@Getter
@Cacheable(false)
public class MallGood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Goods_Id")
    private Integer goodId;

    @Column(name = "Type_Id")
    private Integer typeId;

    @Column(name = "Brand_Id")
    private Integer brandId;

    /**
     * 商品简介
     */
    @Column(name = "Brief")
    private String brief;

    @OneToMany(mappedBy = "good", cascade = CascadeType.PERSIST)
    private List<MallProduct> products;
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
    /**
     * 库存
     */
    @Column(name = "Store")
    private int store;
    @Column(name = "Customer_Id")
    private Integer customerId;
    @Column(name = "StoreId")
    private Long storeId;
    @Column(name = "Name")
    private String name;
    @Column(name = "Disabled")
    private boolean disabled;
    @Column(name = "Marketable")
    private boolean marketable;
    /**
     * 店铺分类
     */
    @Column(name = "Store_Cat_Id")
    private Integer storeCatId;

    @Column(name = "Bn")
    private String bn;

    @Column(name = "CreateTime")
    private Date createTime;

    @Column(name = "Thumbnail_Pic")
    private String thumbnailPic;

    @Column(name = "Small_Pic")
    private String smallPic;

    @Column(name = "Big_Pic")
    private String bigPic;

    @Column(name = "Intro")
    private String intro;

    @Column(name = "Subtitle")
    private String subTitle;

    @Column(name = "Weight")
    private double weight;

    @Column(name = "Unit")
    private String unit;

    @Column(name = "Spec")
    private String spec;

    @Column(name = "Spec_Desc")
    private String specDesc;

    @Column(name = "Notify_Num")
    private Integer notifyNum;

    @Column(name = "LimitBuyNum")
    private Integer limitBuyNum;

    @Column(name = "Freight_Template_Id")
    private Long freightTemplateId;

    @Column(name = "DisRebatePercent")
    private Double disRebatePercent;
}
