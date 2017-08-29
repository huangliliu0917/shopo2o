package com.huotu.shopo2o.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by helloztt on 2017-08-21.
 */
@Configuration
@ComponentScan({
        "com.huotu.shopo2o.service",
        "com.huotu.shopo2o.common"
})
@EnableJpaRepositories(basePackages = "com.huotu.shopo2o.service.repository")
@EnableTransactionManagement
@ImportResource({"classpath:hbm_config_test.xml", "classpath:hbm_config_development.xml","classpath:hbm_config_prod.xml"})
public class ServiceConfig {
}
