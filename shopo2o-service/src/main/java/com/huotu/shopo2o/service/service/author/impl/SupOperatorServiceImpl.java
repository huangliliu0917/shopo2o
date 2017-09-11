package com.huotu.shopo2o.service.service.author.impl;

import com.huotu.shopo2o.service.entity.author.SupOperator;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.repository.author.SupOperatorRepository;
import com.huotu.shopo2o.service.service.author.SupOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class SupOperatorServiceImpl implements SupOperatorService {

    @Autowired
    private SupOperatorRepository supOperatorRepository;

    @Override
    public List<SupOperator> findByCustomerId(Long customerId) {
        return supOperatorRepository.findByCustomer_customerId(customerId);
    }

    @Override
    public SupOperator findById(Long id) {
        SupOperator supOperator = supOperatorRepository.findOne(id);
        String authoritiesStr = "";
        for (Authority authority : supOperator.getAuthoritySet()) {
            authoritiesStr += authority.getCode() + ",";
        }
        supOperator.setAuthoritiesStr(authoritiesStr.substring(0, authoritiesStr.length() - 1));
        return supOperator;
    }

    @Override
    public long countByUsername(String username) {
        return supOperatorRepository.countByUsername(username);
    }

    @Override
    public SupOperator save(SupOperator supOperator) {
        return supOperatorRepository.save(supOperator);
    }

    @Override
    public void delete(Long id) {
        supOperatorRepository.delete(id);
    }

}
