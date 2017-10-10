package com.huotu.shopo2o.service.service.goods;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
public interface HbmGoodsTypeService {
    List<HbmGoodsType> getGoodsTypeByParentId(String parentStandardTypeId);

    List<HbmGoodsType> getGoodsTypeLastUsed(Long customerId);
}
