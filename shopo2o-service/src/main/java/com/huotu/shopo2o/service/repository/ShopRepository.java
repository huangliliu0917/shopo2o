package com.huotu.shopo2o.service.repository;

import com.huotu.shopo2o.service.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017-08-21.
 */
public interface ShopRepository extends JpaRepository<Shop,Long>,JpaSpecificationExecutor<Shop> {
    Shop findByIdAndCustomer_CustomerId(Long shopId,Long customerId);
}
