package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
public interface HbmSupplierGoodsRepository extends JpaRepository<HbmSupplierGoods,Integer>,JpaSpecificationExecutor {
    @Query("update HbmSupplierGoods supplierGoods set supplierGoods.shopCat = null where supplierGoods.shopCat.catId in ?1")
    @Modifying
    void updateSupplierCatId(List<Integer> supCatIdList);
}
