package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.enums.OrderEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

/**
 * Created by hxh on 2017-09-07.
 */
public interface MallOrderRepository extends JpaRepository<MallOrder, String>, JpaSpecificationExecutor<MallOrder> {
    int countByStoreIdAndCreateTimeBetween(int storeId, Date start, Date end);
    int countByStoreIdAndPayStatusAndShipStatusAndCreateTimeBetween(int storeId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end);
}
