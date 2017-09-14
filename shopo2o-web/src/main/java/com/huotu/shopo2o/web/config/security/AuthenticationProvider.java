package com.huotu.shopo2o.web.config.security;

import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.enums.RoleType;
import com.huotu.shopo2o.service.service.MallCustomerService;
import com.huotu.shopo2o.service.service.author.OperatorService;
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
    @Autowired
    private MallCustomerService mallCustomerService;
    @Autowired
    private OperatorService operatorService;

    /**
     * 验证密码
     *
     * @param userDetails
     * @param authentication
     * @throws AuthenticationException
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
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
        UserDetailsService userDetailsService = getUserDetailByRole((AuthenticationToken) authentication);
        UserDetails loadedUser;
        loadedUser = userDetailsService.loadUserByUsername(username);
        if (loadedUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return loadedUser;
    }

    private UserDetailsService getUserDetailByRole(AuthenticationToken authenticationToken){
        if(authenticationToken.getRoleType() == RoleType.STORE.getCode()){
            return mallCustomerService;
        }else{
            return operatorService;
        }
    }
}
