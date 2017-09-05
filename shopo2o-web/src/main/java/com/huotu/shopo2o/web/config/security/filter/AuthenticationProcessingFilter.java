package com.huotu.shopo2o.web.config.security.filter;

import com.huotu.shopo2o.web.common.UserNameAndPasswordNullException;
import com.huotu.shopo2o.web.config.security.AuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hxh on 2017-09-04.
 */
public class AuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        if (username == "") {
            throw new UserNameAndPasswordNullException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.nullUsername",
                    "username can't be null"));
        } else if (password == "") {
            throw new UserNameAndPasswordNullException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.nullPassword",
                    "password can't be null"));
        }
        AuthenticationToken supAuthenticationToken = new AuthenticationToken(username, password);
        setDetails(request, supAuthenticationToken);
        return this.getAuthenticationManager().authenticate(supAuthenticationToken);
    }
}
