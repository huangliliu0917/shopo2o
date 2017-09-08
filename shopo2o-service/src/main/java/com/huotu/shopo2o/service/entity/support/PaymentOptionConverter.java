/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.shopo2o.service.entity.support;


import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.service.enums.OrderEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by hxh on 2017-09-08.
 */
@Converter(autoApply = true)
public class PaymentOptionConverter implements AttributeConverter<OrderEnum.PaymentOptions, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderEnum.PaymentOptions attribute) {
        return attribute.getCode();
    }

    @Override
    public OrderEnum.PaymentOptions convertToEntityAttribute(Integer dbData) {
        return EnumHelper.getEnumType(OrderEnum.PaymentOptions.class, dbData);
    }
}
