package com.huotu.shopo2o.service.service.goods;

import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import org.springframework.data.domain.Page;

/**
 * Created by xyr on 2017/10/10.
 */
public interface HbmSupplierGoodsService {

    Page<HbmSupplierGoods> getGoodList(long storeId, HbmSupplierGoodsSearcher searcher);

}
