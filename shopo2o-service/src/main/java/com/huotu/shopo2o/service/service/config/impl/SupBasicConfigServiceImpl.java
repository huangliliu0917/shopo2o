package com.huotu.shopo2o.service.service.config.impl;

import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import com.huotu.shopo2o.service.repository.config.SupBasicConfigRepository;
import com.huotu.shopo2o.service.service.config.SupBasicConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hxh on 2017-09-18.
 */
@Service
public class SupBasicConfigServiceImpl implements SupBasicConfigService{
    @Autowired
    private SupBasicConfigRepository supBasicConfigRepository;

    @Override
    public SupBasicConfig findByStoreId(Long storeId) {
        SupBasicConfig supBasicConfig = supBasicConfigRepository.findByStoreId(storeId);
        if(supBasicConfig == null){
            supBasicConfig = SupBasicConfig.init(storeId);
            supBasicConfig = supBasicConfigRepository.save(supBasicConfig);
        }
        return supBasicConfig;
    }

    @Override
    @Transactional
    public SupBasicConfig save(SupBasicConfig supBasicConfig, Long storeId) {
        SupBasicConfig old = null;
        if(storeId != null){
            old = supBasicConfigRepository.findByStoreId(storeId);
        }
        if(old == null){
            old = SupBasicConfig.init(storeId);
        }
        old.setContact(supBasicConfig.getContact());
        old.setMobile(supBasicConfig.getMobile());
        old.setEmail(supBasicConfig.getEmail());
        old.setAddr(supBasicConfig.getAddr());
        old.setProvinceCode(supBasicConfig.getProvinceCode());
        old.setCityCode(supBasicConfig.getCityCode());
        old.setDistrictCode(supBasicConfig.getDistrictCode());
        old.setAddressArea(supBasicConfig.getAddressArea());
        old.setLat(supBasicConfig.getLat());
        old.setLan(supBasicConfig.getLan());
        old.setServiceTel(supBasicConfig.getServiceTel());
        old.setAfterSalTel(supBasicConfig.getAfterSalTel());
        old.setAfterSalQQ(supBasicConfig.getAfterSalQQ());
        old.setLogoName(supBasicConfig.getLogoName());
        if(supBasicConfig.getShopConfig() != null){
            old.setShopConfig(supBasicConfig.getShopConfig());
        }
        return supBasicConfigRepository.save(old);    }
}
