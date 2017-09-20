package com.huotu.shopo2o.service.repository.config;

import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hxh on 2017-09-18.
 */
public interface SupBasicConfigRepository extends JpaRepository<SupBasicConfig,Integer>{
    SupBasicConfig findByStoreId(Long storeId);
}
