/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 *
 */

package com.huotu.shopo2o.service.model;

import com.huotu.shopo2o.common.utils.Constant;
import lombok.Data;

/**
 * Created by xyr on 2015/12/25.
 */
@Data
public class HbmSupplierGoodsSearcher {
    private String standardTypeId;
    private int brandId;
    private String typeName;
    private String brandName;
    private String name;
    private String bn;
    private Integer minStore;
    private Integer maxStore;
    private int status = -1;
    private int pageNo = 1;
    private int pageSize = Constant.PAGE_SIZE;
    private Boolean marketable;
    private int supplierCatId = -1;
}
