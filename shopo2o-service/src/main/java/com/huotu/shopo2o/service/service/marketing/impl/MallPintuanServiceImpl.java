package com.huotu.shopo2o.service.service.marketing.impl;

import com.huotu.shopo2o.service.entity.marketing.MallPintuan;
import com.huotu.shopo2o.service.repository.marketing.MallPintuanRepository;
import com.huotu.shopo2o.service.service.marketing.MallPintuanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class MallPintuanServiceImpl implements MallPintuanService {
    @Autowired
    private MallPintuanRepository mallPintuanRepository;

    @Override
    public MallPintuan findByOrderId(String orderId) {
        return mallPintuanRepository.findByOrderId(orderId);
    }
}
