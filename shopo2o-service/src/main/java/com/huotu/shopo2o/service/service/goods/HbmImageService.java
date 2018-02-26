package com.huotu.shopo2o.service.service.goods;

import com.huotu.shopo2o.service.entity.good.HbmImage;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
public interface HbmImageService {
    void batchUpdateImg(List<HbmImage> imgList, Integer supplierGoodId);

    List<HbmImage> findBySupplierGoodId(Integer supplierGoodsId);
}