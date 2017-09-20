package com.huotu.shopo2o.service.service.config;

import com.huotu.shopo2o.service.entity.config.SupBasicConfig;

/**
 * Created by hxh on 2017-09-18.
 */
public interface SupBasicConfigService {
    SupBasicConfig findByStoreId(Long storeId);

    SupBasicConfig save(SupBasicConfig supBasicConfig,Long storeId);
}
