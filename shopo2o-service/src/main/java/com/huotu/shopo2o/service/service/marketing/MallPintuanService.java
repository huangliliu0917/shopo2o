package com.huotu.shopo2o.service.service.marketing;

import com.huotu.shopo2o.service.entity.marketing.MallPintuan;

/**
 * Created by hxh on 2017-09-11.
 */
public interface MallPintuanService {
    MallPintuan findByOrderId(String orderId);
}
