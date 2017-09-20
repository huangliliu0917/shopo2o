package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.MallGood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
public interface MallGoodRepository extends JpaRepository<MallGood,Integer>{
    @Query("update MallGood  goods set goods.storeId = null where goods.storeCatId in ?1")
    @Modifying
    void updateSupplierCatId(List<Integer> supCatIdList);
}
