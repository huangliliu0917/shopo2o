package com.huotu.shopo2o.service.entity.support;

import org.springframework.data.convert.Jsr310Converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by helloztt on 2017-08-24.
 */
@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime,Date>{
    @Override
    public Date convertToDatabaseColumn(LocalTime attribute) {
        return Jsr310Converters.LocalTimeToDateConverter.INSTANCE.convert(attribute);
    }

    @Override
    public LocalTime convertToEntityAttribute(Date dbData) {
        return Jsr310Converters.DateToLocalTimeConverter.INSTANCE.convert(dbData);
    }
}
