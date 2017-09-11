package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by hxh on 2017-09-07.
 */
public interface MallOrderRepository extends JpaRepository<MallOrder, String>, JpaSpecificationExecutor<MallOrder> {
}
