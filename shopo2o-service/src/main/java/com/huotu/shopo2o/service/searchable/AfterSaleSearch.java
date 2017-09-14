package com.huotu.shopo2o.service.searchable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by hxh on 2017-09-14.
 */
@Getter
@Setter
public class AfterSaleSearch {
    private String beginTime;
    private String endTime;
    private String mobile;
    private String afterId;
    private String orderId;
    private Integer afterSaleStatus = -1;
    private String createBeginTime;
    private String createEndTime;
    private String payBeginTime;
    private String payEndTime;
}
