package com.huotu.shopo2o.service.repository.store;

import com.huotu.shopo2o.service.entity.store.SupShopCat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
public interface SupShopCatRepository extends JpaRepository<SupShopCat,Integer>{
    List<SupShopCat> findByStoreIdAndParentIdAndDisabledOrderByOrder(Long storeId, Integer parentId, boolean status);
    List<SupShopCat> findByParentId(Integer parentId);
}
