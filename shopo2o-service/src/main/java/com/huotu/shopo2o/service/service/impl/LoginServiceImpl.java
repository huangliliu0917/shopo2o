package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by hxh on 2017-08-31.
 */
@Service("loginServiceImpl")
public class LoginServiceImpl implements LoginService {
    @Autowired
    private MallCustomerRepository mallCustomerRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MallCustomer mallCustomer = mallCustomerRepository.findByUsername(userName);
        if (mallCustomer == null)
            throw new UsernameNotFoundException("没有该代理商");
        return mallCustomer;
    }
}
