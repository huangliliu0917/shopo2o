package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Repository
public interface HbmGoodsTypeRepository extends JpaRepository<HbmGoodsType, Integer>, HbmGoodsTypeRepositoryCustom{

    List<HbmGoodsType> findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc(String parentStandardTypeId, boolean b, int i);

    HbmGoodsType findByStandardTypeId(String standardTypeId);
}
