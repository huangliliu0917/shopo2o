package com.huotu.shopo2o.service.service.author.impl;

import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.repository.author.OperatorRepository;
import com.huotu.shopo2o.service.service.author.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class OperatorServiceImpl implements OperatorService {

    @Autowired
    private OperatorRepository operatorRepository;

    @Override
    public List<Operator> findByCustomerId(Long customerId) {
        return operatorRepository.findByCustomer_customerIdAndIsDeletedFalse(customerId);
    }

    @Override
    public Operator findById(Long id) {
        Operator operator = operatorRepository.findOne(id);
        String authoritiesStr = "";
        Set<Authority> authoritySet = operator.getAuthoritySet();
        for (Authority authority : authoritySet) {
            if(authority!=null){
                authoritiesStr += authority.getCode() + ",";
            }else {
                authoritySet.remove(authority);
            }
        }
        if(!StringUtils.isEmpty(authoritiesStr)){
            operator.setAuthoritiesStr(authoritiesStr.substring(0, authoritiesStr.length() - 1));
        }
        return operator;
    }

    @Override
    public long countByUsername(String username) {
        return operatorRepository.countByUsername(username);
    }

    @Override
    public Operator save(Operator operator) {
        return operatorRepository.save(operator);
    }

    @Override
    public void delete(Long id) {
        operatorRepository.delete(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return operatorRepository.findByUsername(username);
    }
}
