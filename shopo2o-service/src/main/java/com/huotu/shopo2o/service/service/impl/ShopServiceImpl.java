package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.MallCustomer_;
import com.huotu.shopo2o.service.entity.Shop;
import com.huotu.shopo2o.service.entity.Shop_;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.ShopRepository;
import com.huotu.shopo2o.service.service.MallCustomerService;
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
    @Autowired
    private MallCustomerService customerService;

    @Override
    public ApiResult newShop(Long customerId, Shop shop, String loginName) {
        if (shop.getId() == null || shop.getId() == 0) {
            //调用商城接口，判断该登录名是否是有效的
            ApiResult apiResult = customerService.newCustomer(loginName, null, CustomerTypeEnum.SHOP);
            if (apiResult.getCode() == 200 && apiResult.getData() instanceof MallCustomer) {
                MallCustomer shopCustomer = (MallCustomer) apiResult.getData();
                MallCustomer customer = customerService.findOne(customerId);
                shop.setId(shopCustomer.getCustomerId());
                shop.setCustomer(customer);
            } else {
                return apiResult;
            }
        }
        shopRepository.save(shop);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public Page<Shop> findAll(Long customerId, Pageable pageable) {
        return shopRepository.findAll((root, query, cb) -> cb.and(
                cb.isFalse(root.get(Shop_.isDeleted))
                , cb.isFalse(root.get(Shop_.isDisabled))
                , cb.equal(root.get(Shop_.customer).get(MallCustomer_.customerId), customerId)
        ), pageable);
    }

    @Override
    public Shop findOne(Long shopId) {
        return shopRepository.findOne(shopId);
    }

    @Override
    public Shop findOne(Long shopId, Long customerId) {
        return shopRepository.findByIdAndCustomer_CustomerIdAndIsDeletedFalse(shopId, customerId);
    }

    @Override
    public void disableShop(Shop shop, boolean isDisabled) {
        shop.setDisabled(isDisabled);
        shopRepository.save(shop);
    }

    @Override
    public void deleteShop(Shop shop) {
        shop.setDisabled(true);
        shop.setDeleted(true);
        shopRepository.save(shop);
    }
}
