package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import org.springframework.data.domain.Page;

/**
 * Created by hxh on 2017-09-07.
 */
public interface OrderService {
    Page<MallOrder> findAll(int pageIndex, OrderSearchCondition searchCondition);
}
