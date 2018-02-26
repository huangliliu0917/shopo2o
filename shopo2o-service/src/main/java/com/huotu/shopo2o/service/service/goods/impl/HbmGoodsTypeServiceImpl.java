package com.huotu.shopo2o.service.service.goods.impl;

import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSpecValues;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.repository.good.HbmBrandRepository;
import com.huotu.shopo2o.service.repository.good.HbmGoodsTypeRepository;
import com.huotu.shopo2o.service.repository.good.HbmSpecificationRepository;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Service
public class HbmGoodsTypeServiceImpl implements HbmGoodsTypeService {

    @Autowired
    private HbmGoodsTypeRepository typeRepository;

    @Autowired
    private HbmBrandRepository brandRepository;

    @Autowired
    private HbmSpecificationRepository specRepository;

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

    @Override
    public HbmGoodsType getGoodsTypeWithBrandAndSpecByStandardTypeId(String standardTypeId, Long customerId) {
        HbmGoodsType type = typeRepository.findByStandardTypeId(standardTypeId);
        type = setBrandAndSpec(type, customerId);
        return type;
    }

    @Override
    public HbmGoodsType getGoodsTypeWithBrandAndSpecByStandardTypeId(int typeId, Long customerId) {
        HbmGoodsType type = typeRepository.findOne(typeId);
        type = setBrandAndSpec(type, customerId);
        return type;
    }

    @Override
    public List<HbmGoodsType> getAllParentTypeList(String path) {
        List<HbmGoodsType> typeList = new ArrayList<>();
        //根据path，检索出当前及所有父路径下的类型值
        if (path != null && path.length() > 0) {
            String[] parentStandardTypeIdList = path.split("\\|"); //“.”或“|”必须前面加\\才能分开成数组，即String.split("\\|")
            for (int i = 0; i < parentStandardTypeIdList.length; i++) {
                if (parentStandardTypeIdList[i] != null && parentStandardTypeIdList[i].length() > 0) {
                    List<HbmGoodsType> tempTypeList = typeRepository.findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc(parentStandardTypeIdList[i], false, -1);
                    if (tempTypeList != null && tempTypeList.size() > 0) {
                        typeList.addAll(tempTypeList);
                    }
                }
            }
        } else {
            List<HbmGoodsType> tempTypeList = typeRepository.findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc("0", false, -1);
            typeList.addAll(tempTypeList);
        }
        return typeList;
    }

    private HbmGoodsType setBrandAndSpec(HbmGoodsType type, Long customerId) {
        if (type != null) {
            List<HbmBrand> brandList = findBrandList(type.getTypeId(), customerId);
            type.setBrandList(brandList);
            List<HbmSpecification> specList = findSpecList(type.getTypeId(),customerId);
            if (specList != null && specList.size() > 0) {
                type.setSpecList(specList);
            }
        }
        return type;
    }

    private List<HbmBrand> findBrandList(Integer typeId,Long customerId){
        List<HbmBrand> brandList = brandRepository.findByTypeId(typeId, customerId);
        return brandList;
    }

    private List<HbmSpecification> findSpecList(Integer typeId,Long customerId){
        List<HbmSpecification> specList = typeRepository.findSpecListByTypeId(typeId,customerId);
        if (specList != null && specList.size() > 0) {
            specList.forEach(spec -> {
                List<HbmSpecValues> specValuesList = specRepository.findSpecValueListByTypeIdAndSpecId(typeId, spec.getSpecId(),customerId);
                if (specValuesList != null && specValuesList.size() > 0) {
                    spec.setSpecValues(specValuesList);
                }
            });
        }
        return specList;
    }
}
