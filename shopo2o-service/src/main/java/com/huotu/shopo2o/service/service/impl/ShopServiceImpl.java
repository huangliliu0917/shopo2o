package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.service.entity.MallCustomer_;
import com.huotu.shopo2o.service.entity.Shop;
import com.huotu.shopo2o.service.entity.Shop_;
import com.huotu.shopo2o.service.repository.ShopRepository;
import com.huotu.shopo2o.service.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Override
    public Page<Shop> findAll(Long customerId, Pageable pageable) {
        return shopRepository.findAll((root, query, cb) -> cb.and(
                cb.isFalse(root.get(Shop_.isDeleted))
                ,cb.isFalse(root.get(Shop_.isDisabled))
                ,cb.equal(root.get(Shop_.customer).get(MallCustomer_.customerId),customerId)
        ),pageable);
    }

    @Override
    public Shop findOne(Long shopId) {
        return shopRepository.findOne(shopId);
    }

    @Override
    public Shop findOne(Long shopId, Long customerId) {
        return shopRepository.findByIdAndCustomer_CustomerId(shopId, customerId);
    }

    @Override
    public void disableShop(Shop shop,boolean isDisabled) {
        shop.setDisabled(isDisabled);
    }

    @Override
    public void deleteShop(Shop shop) {
        shop.setDisabled(true);
        shop.setDeleted(true);
    }
}
