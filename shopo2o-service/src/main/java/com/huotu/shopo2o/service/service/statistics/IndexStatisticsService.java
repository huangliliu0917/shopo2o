package com.huotu.shopo2o.service.service.statistics;

import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.model.IndexStatistics;

import java.util.Date;

/**
 * Created by hxh on 2017-09-14.
 */
public interface IndexStatisticsService {
    /**
     * 今日创建订单数量
     *
     * @return
     */
    int todayOrderCount(int storeId, Date start, Date end);

    /**
     * 今日已付款未发货订单数量
     *
     * @return
     */
    int todayUnDeliveryOrderCount(int storeId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end);

    /**
     * 昨日
     *
     * @param storeId
     * @return
     */
    int yesterdayOrderCount(int storeId, Date start, Date end);

    /**
     * 昨日
     *
     * @param storeId
     * @return
     */
    int yesterdayUnDeliveryOrderCount(int storeId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end);

    IndexStatistics orderStatistics(int storeId);
}
