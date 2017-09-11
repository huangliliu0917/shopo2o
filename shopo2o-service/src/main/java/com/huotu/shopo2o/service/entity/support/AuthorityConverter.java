package com.huotu.shopo2o.service.entity.support;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.service.enums.Authority;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hxh on 2017-09-11.
 */
@Converter(autoApply = true)
public class AuthorityConverter implements AttributeConverter<Set<Authority>, String> {
    @Override
    public String convertToDatabaseColumn(Set<Authority> attribute) {
        if (attribute == null) {
            return null;
        }
        String authoritiesStr = "";
        if (attribute.size() > 0) {
            for (Authority authority : attribute) {
                authoritiesStr += authority.getCode() + ",";
            }

            return authoritiesStr.substring(0, authoritiesStr.length() - 1);
        }
        return "";
    }

    @Override
    public Set<Authority> convertToEntityAttribute(String dbData) {
        Set<Authority> authorities = new HashSet<>();
        if (StringUtils.isEmpty(dbData)) {
            return authorities;
        }
        String[] authorityArray = dbData.split(",");
        for (String auth : authorityArray) {
            authorities.add(EnumHelper.getEnumType(Authority.class, auth));
        }
        return authorities;
    }
}
