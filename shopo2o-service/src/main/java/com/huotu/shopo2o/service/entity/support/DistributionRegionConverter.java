package com.huotu.shopo2o.service.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.shopo2o.service.entity.DistributionRegion;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

/**
 * Created by helloztt on 2017-08-31.
 */
@Converter(autoApply = true)
public class DistributionRegionConverter implements AttributeConverter<List<DistributionRegion>,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(List<DistributionRegion> attribute) {
        if (CollectionUtils.isEmpty(attribute)||attribute.size()==0) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public List<DistributionRegion> convertToEntityAttribute(String dbData) {
        if(StringUtils.isEmpty(dbData)){
            return null;
        }
        try {
            return objectMapper.readValue(dbData,new TypeReference<List<DistributionRegion>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
