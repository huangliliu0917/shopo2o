package com.huotu.shopo2o.web.config.security.filter;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.utils.CookieUtils;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.web.common.UserNameAndPasswordNullException;
import com.huotu.shopo2o.web.common.VerifyCodeErrorException;
import com.huotu.shopo2o.web.config.security.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hxh on 2017-09-04.
 */
public class AuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SPRING_SECURITY_FORM_ROLE_TYPE_KEY = "roleType";
    public static final String SPRING_SECURITY_FORM_VERIFY_CODE_KEY = "verifyCode";
    private String verifyCodeParameter = SPRING_SECURITY_FORM_VERIFY_CODE_KEY;
    private String roleTypeParameter = SPRING_SECURITY_FORM_ROLE_TYPE_KEY;
    @Autowired
    private Environment env;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        //校验 验证码
        //从SESSION中获取验证码
        Object realVerifyCode = request.getSession().getAttribute("verifyCode");
        String verifyCode = obtainVerifyCode(request);
        //开发时就不校验验证码了
        if(env.acceptsProfiles("container")){
            if (realVerifyCode == null || StringUtils.isEmpty(realVerifyCode.toString()) || !realVerifyCode.toString().equalsIgnoreCase(verifyCode)) {
                throw new VerifyCodeErrorException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.errorVerifyCode",
                        "verify code is error"));
            }
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        int roleType = obtainRoleType(request);
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
        AuthenticationToken supAuthenticationToken = new AuthenticationToken(username, password, roleType);
        setDetails(request, supAuthenticationToken);
        Authentication authentication = this.getAuthenticationManager().authenticate(supAuthenticationToken);
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            long customerId = 0;
            if (userDetails instanceof MallCustomer) {
                customerId = ((MallCustomer) userDetails).getCustomerId();

            }
            if (userDetails instanceof Operator) {
                customerId = ((Operator) userDetails).getCustomer().getCustomerId();
            }
            CookieUtils.setCookie(response, "storeId", String.valueOf(customerId), SysConstant.COOKIE_DOMAIN.substring(1));
        }
        return authentication;
    }

    private String obtainVerifyCode(HttpServletRequest request) {
        String param = request.getParameter(verifyCodeParameter);
        if (StringUtils.isEmpty(param)) {
            return null;
        }
        return param;
    }

    protected int obtainRoleType(HttpServletRequest request) {
        String param = request.getParameter(roleTypeParameter);
        if (StringUtils.isEmpty(param)) {
            return 0;
        }
        return Integer.parseInt(param);
    }
}
