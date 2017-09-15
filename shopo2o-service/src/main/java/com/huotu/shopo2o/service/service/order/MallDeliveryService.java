package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryService {
    ApiResult pushDelivery(DeliveryInfo deliveryInfo) throws UnsupportedEncodingException;

    Page<MallDelivery> getPage(Pageable pageable, Store store, DeliverySearcher deliverySearcher, String type);

    MallDelivery findById(String id);
}
