/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 *
 */

package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Embeddable
@Getter
@Setter
public class HbmGoodsSpecIndexPK implements Serializable {
    @Column(name = "Spec_Value_Id")
    private Integer specValueId;
//    @Column(name = "Spec_Value")
//    private String specValue;
    @Column(name = "Supplier_Product_Id")
    private Integer productId;
}
