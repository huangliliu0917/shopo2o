package com.huotu.shopo2o.service.service;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2017-08-22.
 */
public interface StoreService {

    /**
     * 添加/保存门店
     *
     * @param customerId 商户ID
     * @param store       门店信息
     * @param loginName  登录账号
     * @return 添加门店结果
     */
    ApiResult saveStore(Long customerId, Store store, String loginName);

    Page<Store> findAll(Long customerId, Pageable pageable);

    Store findOne(Long shopId);

    Store findOne(Long shopId, Long customerId);

    @Transactional
    void disableStore(Store store, boolean isDisabled);

    @Transactional
    void deleteStore(Store store);
}
