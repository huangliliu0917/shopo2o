package com.huotu.shopo2o.service.service.order.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class MallDeliveryServiceImpl implements MallDeliveryService {
    @Autowired
    private MallOrderService mallOrderService;

    @Override
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo) throws UnsupportedEncodingException {
        MallOrder order = mallOrderService.findByOrderId(deliveryInfo.getOrderId());
        //判断订单是否发货
        // TODO: 2017-09-11 发货（不清楚怎么实现）
        return null;
    }
}
