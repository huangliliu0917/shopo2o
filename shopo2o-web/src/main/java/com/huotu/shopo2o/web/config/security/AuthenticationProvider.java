package com.huotu.shopo2o.web.config.security;

import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;

/**
 * Created by hxh on 2017-09-04.
 */
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements AuthenticationManager {
    @Autowired
    private MallPasswordEncoder passwordEncoder;
    @Resource(name = "loginServiceImpl")
    private UserDetailsService mallCustomerService;

    /**
     * 验证密码
     *
     * @param userDetails
     * @param authentication
     * @throws AuthenticationException
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String presentedPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    /**
     * 验证用户名
     *
     * @param username
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser;
        loadedUser = this.mallCustomerService.loadUserByUsername(username);
        if (loadedUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return loadedUser;
    }
}
