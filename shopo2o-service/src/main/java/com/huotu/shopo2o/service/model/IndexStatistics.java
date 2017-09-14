package com.huotu.shopo2o.service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by hxh on 2017-09-14.
 */
@Getter
@Setter
public class IndexStatistics {
    private int todayOrderCount;
    private int todayUnDeliveryCount;
    private int yesterdayOrderCount;
    private int yesterdayUnDeliveryCount;
    private int afterSaleCount;
}
