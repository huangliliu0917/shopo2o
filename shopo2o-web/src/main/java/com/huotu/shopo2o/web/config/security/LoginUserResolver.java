package com.huotu.shopo2o.web.config.security;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.Operator;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by helloztt on 2017-09-13.
 */
public class LoginUserResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (userDetails instanceof MallCustomer) {
            return userDetails;
        }
        if (userDetails instanceof Operator) {
            return ((Operator) userDetails).getCustomer();
        }
        return null;
    }
}
