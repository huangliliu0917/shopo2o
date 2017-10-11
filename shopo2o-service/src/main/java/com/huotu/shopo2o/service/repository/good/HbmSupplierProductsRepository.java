package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xyr on 2017/10/11.
 */
@Repository
public interface HbmSupplierProductsRepository extends JpaRepository<HbmSupplierProducts,Integer>,JpaSpecificationExecutor {

    @Query("SELECT A FROM HbmSupplierProducts A WHERE A.supplierGoodsId = ?1 ORDER BY A.props")
    List<HbmSupplierProducts> findBySupplierGoodsId(int supplierGoodsId);

}
