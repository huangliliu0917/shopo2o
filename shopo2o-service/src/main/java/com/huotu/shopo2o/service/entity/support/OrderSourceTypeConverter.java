package com.huotu.shopo2o.service.entity.support;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.service.enums.OrderEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by hxh on 2017-09-08.
 */
@Converter(autoApply = true)
public class OrderSourceTypeConverter implements AttributeConverter<OrderEnum.OrderSourceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderEnum.OrderSourceType attribute) {
        return attribute.getCode();
    }

    @Override
    public OrderEnum.OrderSourceType convertToEntityAttribute(Integer dbData) {
        return EnumHelper.getEnumType(OrderEnum.OrderSourceType.class, dbData);
    }
}
