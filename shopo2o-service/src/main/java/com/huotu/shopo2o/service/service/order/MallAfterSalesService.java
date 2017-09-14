package com.huotu.shopo2o.service.service.order;

import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.searchable.AfterSaleSearch;
import org.springframework.data.domain.Page;

/**
 * Created by hxh on 2017-09-14.
 */
public interface MallAfterSalesService {
    /**
     * 分页按条件查询
     *
     * @param pageIndex
     * @param pageSize
     * @param afterSaleSearch
     * @return
     */
    Page<MallAfterSales> findAll(int pageIndex, int pageSize, Long supplierId, AfterSaleSearch afterSaleSearch);

    /**
     * 根据id得到售后单实体
     *
     * @param afterId
     * @return
     */
    MallAfterSales findByAfterId(String afterId);
    int UnhandledCount(int storeId);
}
