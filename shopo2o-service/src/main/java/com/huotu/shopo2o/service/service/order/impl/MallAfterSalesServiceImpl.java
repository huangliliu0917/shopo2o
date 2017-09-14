package com.huotu.shopo2o.service.service.order.impl;

import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
@Service
public class MallAfterSalesServiceImpl implements MallAfterSalesService{
    @Autowired
    private MallAfterSalesRepository mallAfterSalesRepository;
    @Override
    public int UnhandledCount(int storeId) {
        List<AfterSaleEnum.AfterSaleStatus> afterSaleStatuses = new ArrayList<>();
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.CANCELED);
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.REFUND_SUCCESS);
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED);
        return mallAfterSalesRepository.countByStoreIdAndAfterSaleStatusNotIn(storeId, afterSaleStatuses);
    }
}
