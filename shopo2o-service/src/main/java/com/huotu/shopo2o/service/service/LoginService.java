package com.huotu.shopo2o.service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by hxh on 2017-08-31.
 */
public interface LoginService extends UserDetailsService{
    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;
}
