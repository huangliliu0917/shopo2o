package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryRepository extends JpaRepository<MallDelivery,String>{
    List<MallDelivery> findByOrder_OrderIdAndTypeIgnoreCase(String orderId,String type);
}
