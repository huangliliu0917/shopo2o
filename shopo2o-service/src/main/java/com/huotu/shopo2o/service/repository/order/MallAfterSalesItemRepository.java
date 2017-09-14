package com.huotu.shopo2o.service.repository.order;

import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
public interface MallAfterSalesItemRepository extends JpaRepository<MallAfterSalesItem, Integer> {
    List<MallAfterSalesItem> findByAfterSales_AfterIdOrderByItemIdDesc(String afterId);
}
