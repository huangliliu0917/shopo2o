package com.huotu.shopo2o.service.repository.config;

import com.huotu.shopo2o.service.entity.config.MallCustomerConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
public interface MallCustomerConfigRepository extends JpaRepository<MallCustomerConfig,Long> {

    MallCustomerConfig findByCustomerId(Long customerId);
}
