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
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Entity
@Table(name="Mall_Type_Brand")
@IdClass(HbmTypeBrandPK.class)
@Cacheable(false)
@Getter
@Setter
public class HbmTypeBrand {
    /**
     * 类型主键
     */
    @Id
    @Column(name = "Type_Id")
    private int typeId;

    @Id
    @Column(name = "Brand_Id")
    private int brandId;

    @Column(name = "Brand_Order")
    private int brandOrder;
}
