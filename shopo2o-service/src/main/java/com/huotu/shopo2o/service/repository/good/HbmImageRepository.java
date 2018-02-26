package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Repository
public interface HbmImageRepository extends JpaRepository<HbmImage,Integer>,JpaSpecificationExecutor {

    @Modifying
    @Transactional
    @Query("DELETE FROM HbmImage A WHERE A.supplierGoodId=?1 AND A.gimageId NOT IN ?2")
    int deleteBySupplierGoodIdAndGimageIdNotIn(int supplierGoodId,List<Integer> imgIds);

    int deleteBySupplierGoodId(int supplierGoodId);

    List<HbmImage> findBySupplierGoodId(int supplierGoodId);

    @Query("SELECT A FROM HbmImage A WHERE A.gimageId IN ?1")
    List<HbmImage> findByGimageIdIn(List<String> imgIds);
}
