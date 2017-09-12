package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.model.OrderDetailModel;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
public interface MallOrderService {

    MallOrder findByOrderId(String orderId);

    Page<MallOrder> findAll(Pageable pageable, OrderSearchCondition searchCondition);

    HSSFWorkbook createWorkBook(List<MallOrder> orders);

    OrderDetailModel findOrderDetail(String orderId);
}
