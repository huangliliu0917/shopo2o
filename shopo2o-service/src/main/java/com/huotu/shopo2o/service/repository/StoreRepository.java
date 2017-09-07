package com.huotu.shopo2o.service.repository;

import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by helloztt on 2017-08-21.
 */
public interface StoreRepository extends JpaRepository<Store,Long>,JpaSpecificationExecutor<Store> {
    Store findByIdAndCustomer_CustomerId(Long storeId, Long customerId);

    Store findByIdAndCustomer_CustomerIdAndIsDeletedFalse(Long storeId, Long customerId);
}
