package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Repository
public interface HbmBrandRepository extends JpaRepository<HbmBrand, Integer>, JpaSpecificationExecutor {

    @Query("SELECT a FROM HbmBrand a,HbmTypeBrand b WHERE a.brandId = b.brandId AND b.typeId = ?1 AND (a.customerId = -1 or a.customerId = ?2) " +
            "ORDER BY a.customerId DESC,b.brandOrder ASC ")
    List<HbmBrand> findByTypeId (int typeId, Long customerId);
}
