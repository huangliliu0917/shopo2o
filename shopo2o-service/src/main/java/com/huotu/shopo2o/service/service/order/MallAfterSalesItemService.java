package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;

import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
public interface MallAfterSalesItemService {
    List<MallAfterSalesItem> findByAfterId(String afterId);

    /**
     * 得到最近一条非留言记录
     */
    MallAfterSalesItem findTopByIsLogic(MallAfterSales afterSales, int isLogic);

    /**
     * 保存实体
     *
     * @param afterSalesItem
     * @return
     */
    MallAfterSalesItem save(MallAfterSalesItem afterSalesItem);
}
