package com.huotu.shopo2o.service.service.statistics.impl;

import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.model.IndexStatistics;
import com.huotu.shopo2o.service.repository.order.MallOrderRepository;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import com.huotu.shopo2o.service.service.statistics.IndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by hxh on 2017-09-14.
 */
@Service
public class IndexStatisticsServiceImpl implements IndexStatisticsService{
    @Autowired
    private MallOrderRepository mallOrderRepository;
    @Autowired
    private MallAfterSalesService mallAfterSalesService;
    @Override
    public int todayOrderCount(int storeId, Date start, Date end) {
        return mallOrderRepository.countByStoreIdAndCreateTimeBetween(storeId,start,end);
    }

    @Override
    public int todayUnDeliveryOrderCount(int storeId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end) {
        return mallOrderRepository.countByStoreIdAndPayStatusAndShipStatusAndCreateTimeBetween(storeId,payStatus,shipStatus,start,end);
    }

    @Override
    public int yesterdayOrderCount(int storeId, Date start, Date end) {
        return mallOrderRepository.countByStoreIdAndCreateTimeBetween(storeId,start,end);
    }

    @Override
    public int yesterdayUnDeliveryOrderCount(int storeId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end) {
        return mallOrderRepository.countByStoreIdAndPayStatusAndShipStatusAndCreateTimeBetween(storeId,payStatus,shipStatus,start,end);
    }

    @Override
    public IndexStatistics orderStatistics(int storeId) {
        //今日
        LocalDate nowDate = LocalDate.now();
        Date todayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.atStartOfDay());
        Date todayEnd = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.plusDays(1).atStartOfDay());
        IndexStatistics indexStatistics = new IndexStatistics();
        indexStatistics.setTodayOrderCount(todayOrderCount(storeId, todayStart, todayEnd));
        indexStatistics.setTodayUnDeliveryCount(todayUnDeliveryOrderCount(storeId, OrderEnum.PayStatus.PAYED, OrderEnum.ShipStatus.NOT_DELIVER, todayStart, todayEnd));
        //昨日
        Date yesterdayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.minusDays(1).atStartOfDay());
        Date yesterdayEnd = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.atStartOfDay());
        indexStatistics.setYesterdayOrderCount(yesterdayOrderCount(storeId, yesterdayStart, yesterdayEnd));
        indexStatistics.setYesterdayUnDeliveryCount(yesterdayUnDeliveryOrderCount(storeId, OrderEnum.PayStatus.PAYED, OrderEnum.ShipStatus.NOT_DELIVER, yesterdayStart, yesterdayEnd));
        indexStatistics.setAfterSaleCount(mallAfterSalesService.UnhandledCount(storeId));
        return indexStatistics;
    }
}
