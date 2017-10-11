package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmSpecValues;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Repository
public interface HbmSpecificationRepository extends JpaRepository<HbmSpecification, Integer>, JpaSpecificationExecutor<HbmSpecification> {
    @Query(value = "SELECT DISTINCT c FROM HbmGoodsType a,HbmGoodsTypeSpec b,HbmSpecValues c  " +
            "WHERE a.typeId = b.typeId and c.id = b.specValueId and b.typeId= ?1 and b.specId=?2 and (b.customerId = -1 or b.customerId = ?3)" +
            "ORDER BY c.customerId DESC,c.order ASC")
    List<HbmSpecValues> findSpecValueListByTypeIdAndSpecId(int typeId, int specId, Long customerId);

    /**
     * 查找平台自定义的类型规格值
     * @param customerId
     * @param specId
     * @return
     */
    @Query(value = "SELECT DISTINCT b FROM HbmSpecification a INNER JOIN HbmSpecValues b ON a.specId = b.specId " +
            "WHERE  a.customerId = ?1 AND a.specId = ?2 " +
            "ORDER BY b.order ASC")
    List<HbmSpecValues> findCustomerSpecValueListByCustomerIdAndSpecId(Long customerId, Integer specId);
}
