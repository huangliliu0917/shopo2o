package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.jsonformat.LogiModel;
import com.huotu.shopo2o.service.jsonformat.OrderForDelivery;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallDeliveryService {
    MallDelivery save(MallDelivery delivery);

    ApiResult pushDelivery(DeliveryInfo deliveryInfo, Long customerId) throws UnsupportedEncodingException;

    Page<MallDelivery> getPage(Pageable pageable, Store store, DeliverySearcher deliverySearcher, String type);

    MallDelivery findById(String id);

    /**
     * 批量发货
     *
     * @param orderForDeliveries
     * @param customerId         服务的分销商id
     * @return
     * @throws UnsupportedEncodingException
     */
    ApiResult pushBatchDelivery(List<OrderForDelivery> orderForDeliveries, Long customerId) throws UnsupportedEncodingException;

    HSSFWorkbook createWorkBook(List<MallDelivery> deliveryList, String type);

    ApiResult pushRefund(String orderId, LogiModel logiModel, Long storeId, String dicReturnItemsStr) throws UnsupportedEncodingException;
}
