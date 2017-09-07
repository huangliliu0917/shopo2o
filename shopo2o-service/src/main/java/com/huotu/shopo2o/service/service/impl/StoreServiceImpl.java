package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.MallCustomer_;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.store.Store_;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.StoreRepository;
import com.huotu.shopo2o.service.service.MallCustomerService;
import com.huotu.shopo2o.service.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MallCustomerService customerService;

    @Override
    public ApiResult saveStore(Long customerId, Store store, String loginName) {
        if (store.getId() == null || store.getId() == 0) {
            //调用商城接口，判断该登录名是否是有效的
            ApiResult apiResult = customerService.newCustomer(loginName, null, CustomerTypeEnum.SHOP);
            if (apiResult.getCode() == 200 && apiResult.getData() instanceof MallCustomer) {
                MallCustomer shopCustomer = (MallCustomer) apiResult.getData();
                MallCustomer customer = customerService.findOne(customerId);
                store.setId(shopCustomer.getCustomerId());
                store.setCustomer(customer);
            } else {
                return apiResult;
            }
        }
        storeRepository.save(store);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public Page<Store> findAll(Long customerId, Pageable pageable) {
        return storeRepository.findAll((root, query, cb) -> cb.and(
                cb.isFalse(root.get(Store_.isDeleted))
//                , cb.isFalse(root.get(Shop_.isDisabled))
                , cb.equal(root.get(Store_.customer).get(MallCustomer_.customerId), customerId)
        ), pageable);
    }

    @Override
    public Store findOne(Long shopId) {
        return storeRepository.findOne(shopId);
    }

    @Override
    public Store findOne(Long shopId, Long customerId) {
        return storeRepository.findByIdAndCustomer_CustomerIdAndIsDeletedFalse(shopId, customerId);
    }

    @Override
    public void disableStore(Store store, boolean isDisabled) {
        store.setDisabled(isDisabled);
        storeRepository.save(store);
    }

    @Override
    public void deleteStore(Store store) {
        store.setDisabled(true);
        store.setDeleted(true);
        storeRepository.save(store);
    }
}
