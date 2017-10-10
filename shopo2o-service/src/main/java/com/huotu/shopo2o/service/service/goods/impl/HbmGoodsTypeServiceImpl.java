package com.huotu.shopo2o.service.service.goods.impl;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeRepository;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Service
public class HbmGoodsTypeServiceImpl implements HbmGoodsTypeService {

    @Autowired
    private HbmGoodsTypeRepository typeRepository;

    @Override
    public List<HbmGoodsType> getGoodsTypeByParentId(String parentStandardTypeId) {
        List<HbmGoodsType> typeList = typeRepository.findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc(parentStandardTypeId, false, -1);
        return typeList;
    }

    @Override
    public List<HbmGoodsType> getGoodsTypeLastUsed(Long customerId) {
        return null;
    }
}
