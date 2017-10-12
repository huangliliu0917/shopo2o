package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmSpecValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
public interface HbmSpecValuesRepository extends JpaRepository<HbmSpecValues,Integer>,JpaSpecificationExecutor {
    HbmSpecValues findBySpecIdAndValue(Integer specId,String specValueName);
}
