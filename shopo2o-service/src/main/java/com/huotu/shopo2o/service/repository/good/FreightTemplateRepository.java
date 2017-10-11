package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.FreightTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Repository
public interface FreightTemplateRepository extends JpaRepository<FreightTemplate, Long> {
    /**
     * 查询供应商运费模板
     *
     * @param customerId 供应商或平台ID
     * @return 查询结果
     */
    @Query("select a from FreightTemplate a where a.customerId = ?1 and a.freightTemplateType = 1")
    List<FreightTemplate> findByCustomerId(Long customerId);

    /**
     * 查询供应商和平台的运费模板
     *
     * @param storeId 供应商ID
     * @param customerId 平台ID
     * @return 查询结果
     */
    @Query("select a from FreightTemplate a where a.customerId = ?1 or a.customerId = ?2")
    List<FreightTemplate> findByStoreIdAndCustomerId(Long storeId, Long customerId);
}
