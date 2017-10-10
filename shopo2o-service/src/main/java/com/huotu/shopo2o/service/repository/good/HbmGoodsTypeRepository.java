package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
public interface HbmGoodsTypeRepository extends JpaRepository<HbmGoodsType, Integer>, HbmGoodsTypeRepositoryCustom{

    List<HbmGoodsType> findByParentStandardTypeIdAndDisabledAndCustomerIdOrderByTOrderAsc(String parentStandardTypeId, boolean b, int i);
}
