package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-08-31.
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private MallCustomerRepository mallCustomerRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MallCustomer mallCustomer = mallCustomerRepository.findByUsername(userName);
        if (mallCustomer == null)
            throw new UsernameNotFoundException("找不到用户信息");
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        getRoles(mallCustomer, list);
        User user = new User(mallCustomer.getUsername(), mallCustomer.getPassword(), list);
        return user;
    }

    private void getRoles(MallCustomer mallCustomer, List<GrantedAuthority> list) {
        list.add(new SimpleGrantedAuthority("ROLE_" + mallCustomer.getCustomerType()));
    }
}
