package com.huotu.shopo2o.service.repository.marketing;

import com.huotu.shopo2o.service.entity.marketing.MallPintuan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallPintuanRepository extends JpaRepository<MallPintuan, Integer> {
    MallPintuan findByOrderId(String orderId);
}
