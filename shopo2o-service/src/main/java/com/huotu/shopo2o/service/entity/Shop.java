package com.huotu.shopo2o.service.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 线下门店。非代理商门店
 * Created by helloztt on 2017-08-21.
 */
@Entity
@Table(name = "Shop_O2OShop")
@Getter
@Setter
@Cacheable(false)
public class Shop {

    @Id
    @Column(name = "Shop_Id")
    private Long id;

    /**
     * 平台方
     */
    @ManyToOne
    @JoinColumn(name = "Customer_Id",columnDefinition = "numeric(19, 0)")
    private MallCustomer customer;

    /**
     * 名称
     */
    @Column(name = "Name")
    private String name;

    /**
     * 线下门店ID
     */
    @Column(name = "Offline_ShopId")
    private String offlineShopId;

    /**
     * 区号
     */
    @Column(name = "Area_Code")
    private String areaCode;

    /**
     * 电话号码
     */
    @Column(name = "Telephone")
    private String telephone;

    /**
     * 省code
     */
    @Column(name = "Province_Code")
    private String provinceCode;

    /**
     * 市code
     */
    @Column(name = "City_Code")
    private String cityCode;

    /**
     * 区code
     */
    @Column(name = "District_Code")
    private String districtCode;

    /**
     * 地址
     *
     * @return
     */
    @Column(name = "Address")
    private String address;

    /**
     * 经度
     */
    @Column(name = "Lan")
    private Double lan;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    private Double lat;

    /**
     * 图标
     */
    @Column(name = "Logo")
    private String logo;

    /**
     * ERP门店ID
     */
    @Column(name = "Erp_Id")
    private String erpId;

    /**
     * 配送费
     */
    @Column(name = "Delivery_Cost",scale = 2, precision = 12)
    private BigDecimal deliveryCost;

    /**
     * 起送金额
     */
    @Column(name = "Min_Cost",scale = 2,precision = 12)
    private BigDecimal minCost;

    /**
     * 免邮金额
     */
    @Column(name = "Free_Cost",scale = 2,precision = 12)
    private BigDecimal freeCost;

    /**
     * 是否冻结
     */
    @Column(name = "Disabled")
    private boolean isDisabled;

    /**
     * 是否删除
     */
    @Column(name = "Deleted")
    private boolean isDeleted;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createTime;
}
