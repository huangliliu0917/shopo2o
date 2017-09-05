package com.huotu.shopo2o.web.config.security;

import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        UserDetails loadedUser;
        loadedUser = this.getCurrentService(authentication).loadUserByUsername(username);
        if (loadedUser != null && loadedUser instanceof MallCustomer) {
            if (((MallCustomer) loadedUser).getCustomerType().getCode() != authenticationToken.getRoleType()) {
                throw new UsernameNotFoundException("用户名或密码错误");
            }
        }
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    private UserDetailsService getCurrentService(UsernamePasswordAuthenticationToken authentication) {
        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        UserDetailsService currentService = null;

        if (authenticationToken.getRoleType() == CustomerTypeEnum.AGENT_SHOP.getCode() || authenticationToken.getRoleType() == CustomerTypeEnum.AGENT.getCode()) {
            currentService = mallCustomerService;
        }
        return currentService;
    }
}
