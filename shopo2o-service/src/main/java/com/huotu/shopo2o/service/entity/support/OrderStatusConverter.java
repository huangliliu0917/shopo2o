package com.huotu.shopo2o.service.entity.support;


import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.service.enums.OrderEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by hxh on 2017-09-08.
 */
@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderEnum.OrderStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderEnum.OrderStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public OrderEnum.OrderStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return EnumHelper.getEnumType(OrderEnum.OrderStatus.class, dbData);
    }
}
