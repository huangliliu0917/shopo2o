package com.huotu.shopo2o.service.service.goods;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
public interface HbmGoodsTypeService {
    List<HbmGoodsType> getGoodsTypeByParentId(String parentStandardTypeId);

    List<HbmGoodsType> getGoodsTypeLastUsed(Long storeId);

    String getTypePath(HbmGoodsType type);

    HbmGoodsType getGoodsTypeByStandardTypeId(String standardTypeId);

    /**
     * 根据标准类目ID获取类目及其品牌规则信息
     * @param standardTypeId
     * @param customerId
     * @return
     */
    HbmGoodsType getGoodsTypeWithBrandAndSpecByStandardTypeId(String standardTypeId,Long customerId);

    HbmGoodsType getGoodsTypeWithBrandAndSpecByStandardTypeId(int typeId,Long customerId);

    List<HbmGoodsType> getAllParentTypeList(String path);
}
