package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryRepository extends JpaRepository<MallDelivery,String>,JpaSpecificationExecutor<MallDelivery> {
    List<MallDelivery> findByOrder_OrderIdAndTypeIgnoreCase(String orderId,String type);
}
