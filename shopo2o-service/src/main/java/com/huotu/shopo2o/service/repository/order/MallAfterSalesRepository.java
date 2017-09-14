package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
public interface MallAfterSalesRepository extends JpaRepository<MallAfterSales,String>, JpaSpecificationExecutor {
    @Query("select count(afterSale) from MallAfterSales afterSale where afterSale.storeId=?1 and afterSale.afterSaleStatus not in ?2")
    int countByStoreIdAndAfterSaleStatusNotIn(int storeId, List<AfterSaleEnum.AfterSaleStatus> afterSaleStatuses);
}
