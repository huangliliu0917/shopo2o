/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.HbmGoodsTypeSpec;
import com.huotu.shopo2o.service.entity.good.HbmGoodsTypeSpecPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by luyuanyuan on 2017/10/12.
 */
public interface HbmGoodsTypeSpecRepository extends JpaRepository<HbmGoodsTypeSpec,HbmGoodsTypeSpecPK>,JpaSpecificationExecutor {

    @Modifying
    @Query("delete from HbmGoodsTypeSpec a where a.typeId = ?1 and a.specId = ?2 and a.customerId = ?3")
    void deleteByTypeIdAndSpecId(Integer typeId, Integer specId, Integer customerId);
}
