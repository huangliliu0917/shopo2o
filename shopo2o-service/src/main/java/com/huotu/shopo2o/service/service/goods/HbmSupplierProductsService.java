package com.huotu.shopo2o.service.service.goods;

import com.huotu.shopo2o.service.entity.good.HbmGoodsSpecIndex;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;

import java.util.List;

/**
 * Created by xyr on 2017/10/11.
 */
public interface HbmSupplierProductsService {
    HbmSupplierProducts getProductByProductId(Integer supplierProductId);

    /**
     * 根据商品ID获取货品列表
     */
    List<HbmSupplierProducts> getProductListByGoodId(int goodId);

    void removeProduct(HbmSupplierProducts products);

    /**
     * 保存货品列表
     */
    List<HbmSupplierProducts> saveList(List<HbmSupplierProducts> productsList);

    List<HbmGoodsSpecIndex> saveSpecIndex(List<HbmGoodsSpecIndex> specIndexList);
}
