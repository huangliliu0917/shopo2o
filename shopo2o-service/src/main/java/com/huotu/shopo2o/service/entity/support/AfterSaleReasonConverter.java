package com.huotu.shopo2o.service.entity.support;


import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by hxh on 2017-09-08.
 */
@Converter(autoApply = true)
public class AfterSaleReasonConverter implements AttributeConverter<AfterSaleEnum.AfterSalesReason, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AfterSaleEnum.AfterSalesReason afterSalesReason) {
        return afterSalesReason.getCode();
    }

    @Override
    public AfterSaleEnum.AfterSalesReason convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        }
        return EnumHelper.getEnumType(AfterSaleEnum.AfterSalesReason.class, integer);
    }
}
