/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Entity
@Table(name = "Mall_Supplier_Gimages")
@Cacheable(false)
@Getter
@Setter
public class HbmImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Gimage_Id")
    private Integer gimageId;

    @Column(name = "Supplier_Goods_Id")
    private int supplierGoodId;

    @Column(name = "Is_Remote")
    private boolean isRemote = false;

    private String source = "";

    private int orderBy = 0;

    @Column(name = "Src_Size_Width")
    private int srcWidth = 0;

    @Column(name = "Src_Size_Height")
    private int srcHeight = 0;

    private String small;

    private String big;

    private String thumbnail;

    @Column(name = "Up_Time")
    private Date upTime;

    @Column(name = "Supplier_Id")
    private int supplierId;

    /**
     * 门店id
     */
    @Column(name = "Store_Id")
    private long storeId;

    @Column(name = "Supplier_Gimage_Id")
    private int supplierGimageId = 0;

    @Transient
    private String uri;

}
