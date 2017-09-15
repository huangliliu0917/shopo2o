package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallDelivery;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryRepository extends PagingAndSortingRepository<MallDelivery, String>, JpaSpecificationExecutor<MallDelivery> {
    List<MallDelivery> findByOrder_OrderIdAndTypeIgnoreCase(String orderId,String type);
}
