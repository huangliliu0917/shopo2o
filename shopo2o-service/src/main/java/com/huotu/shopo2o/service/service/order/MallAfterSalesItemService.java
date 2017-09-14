package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;

import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
public interface MallAfterSalesItemService {
    List<MallAfterSalesItem> findByAfterId(String afterId);
}
