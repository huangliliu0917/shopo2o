package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Repository
public interface HbmGoodsTypeRepository extends JpaRepository<HbmGoodsType, Integer>, HbmGoodsTypeRepositoryCustom{

    List<HbmGoodsType> findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc(String parentStandardTypeId, boolean b, int i);

    HbmGoodsType findByStandardTypeId(String standardTypeId);

    @Query(value = "SELECT DISTINCT c FROM HbmGoodsType a,HbmGoodsTypeSpec b,HbmSpecification c  " +
            "WHERE a.typeId = b.typeId and c.specId = b.specId and a.typeId= ?1 and (b.customerId = -1 or b.customerId = ?2)" +
            "ORDER BY c.customerId DESC,c.order ASC")
    List<HbmSpecification> findSpecListByTypeId(int typeId,Long customerId);
}
