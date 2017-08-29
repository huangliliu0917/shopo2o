package com.huotu.shopo2o.service.repository;

import com.huotu.shopo2o.service.entity.MallCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by helloztt on 2017-08-21.
 */
public interface MallCustomerRepository extends JpaRepository<MallCustomer, Long> {

    MallCustomer findByUsername(String userName);
}
