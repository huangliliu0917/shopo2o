package com.huotu.shopo2o.service.service.shop;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.store.SupShopCat;

import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
public interface SupShopCatService {
    List<SupShopCat> findByStoreId(Long storeId);

    SupShopCat saveCat(SupShopCat supShopCat);

    List<SupShopCat> findTopCatByStoreId(Long storeId);

    SupShopCat findByCatId(Integer catId);

    ApiResult deleteCat(Integer catId);
}
