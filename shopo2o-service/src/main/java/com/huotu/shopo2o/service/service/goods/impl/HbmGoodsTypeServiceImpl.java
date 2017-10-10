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
    public List<HbmGoodsType> getGoodsTypeLastUsed(Long storeId) {
        List<HbmGoodsType> lastUsedType = typeRepository.getAllUsedByStoreId(storeId);
        if (lastUsedType != null && lastUsedType.size() > 0) {
            lastUsedType.forEach(p -> {
                p.setPathStr(getTypePath(p));
            });
        }
        return lastUsedType;
    }

    @Override
    public String getTypePath(HbmGoodsType type) {
        String path = type.getName();
        if (type != null && type.getParentStandardTypeId() != null && !"0".equals(type.getParentStandardTypeId())) {
            path = getParentName(path, type.getParentStandardTypeId());
        }
        return path;
    }

    public String getParentName(String path, String parentStandardTypeId) {
        HbmGoodsType type = getGoodsTypeByStandardTypeId(parentStandardTypeId);
        if (path.length() > 0) {
            path = ">>" + path;
        }
        path = (type == null ? "" : type.getName()) + path;
        if (type != null && type.getParentStandardTypeId() != null && !"0".equals(type.getParentStandardTypeId())) {
            path = getParentName(path, type.getParentStandardTypeId());
        }
        return path;
    }

    @Override
    public HbmGoodsType getGoodsTypeByStandardTypeId(String standardTypeId) {
        HbmGoodsType type = typeRepository.findByStandardTypeId(standardTypeId);
        return type;
    }
}
