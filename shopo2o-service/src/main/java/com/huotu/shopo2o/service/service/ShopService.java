package com.huotu.shopo2o.service.service;

import com.huotu.shopo2o.service.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2017-08-22.
 */
public interface ShopService {

    Page<Shop> findAll(Long customerId, Pageable pageable);

    Shop findOne(Long shopId);

    Shop findOne(Long shopId,Long customerId);

    @Transactional
    void disableShop(Shop shop,boolean isDisabled);

    @Transactional
    void deleteShop(Shop shop);
}
