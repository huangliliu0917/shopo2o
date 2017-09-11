package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.model.DeliveryInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryService {
    ApiResult pushDelivery(DeliveryInfo deliveryInfo) throws UnsupportedEncodingException;
}
