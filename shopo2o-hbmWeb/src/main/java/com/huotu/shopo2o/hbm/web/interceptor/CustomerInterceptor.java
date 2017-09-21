package com.huotu.shopo2o.hbm.web.interceptor;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 嵌入伙伴商城的页面获取customerId
 * Created by helloztt on 2016/7/4.
 */
@Component
public class CustomerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private Environment environment;
    private static final String CUSTOMER_ID = "customerId";
    private static final String ST_CUSTOMER_ID = "ST_CustomerId";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer customerId = 0, loginType;
        String funcAuthorize, requestCustomerId = null;
        //先看看当前登录用户是什么类型
        //如果是超级管理员类型，获取请求参数中customer_Id，并设置Store_Id 到 cookies 中，如果没有参数看看 cookies 中有没有 ST_CUSTOMER_ID
        //如果是商家总账号，直接从 cookies 中读取 UserID

        // 用户登录类型：0:超级管理员;1:商家总账号;3:操作员
        loginType = CookieUtils.getCookieValInteger(request, "LoginType");
        //遍历 parameter keySet,找到 customerId
        Enumeration<String> paramKeys = request.getParameterNames();
        while (paramKeys.hasMoreElements()) {
            String paramKey = paramKeys.nextElement();
            if (CUSTOMER_ID.equalsIgnoreCase(paramKey)) {
                requestCustomerId = request.getParameter(paramKey);
                break;
            }
        }
        switch (loginType){
            case 0:{
                customerId = CookieUtils.getCookieValInteger(request, ST_CUSTOMER_ID);
                if (StringUtils.isEmpty(requestCustomerId) && customerId == 0) {
                    response.sendRedirect("http://login." + SysConstant.COOKIE_DOMAIN);
                    return false;
                } else if(!StringUtils.isEmpty(requestCustomerId)){
                    customerId = Integer.valueOf(requestCustomerId);
                    CookieUtils.setCookie(response,ST_CUSTOMER_ID,requestCustomerId,SysConstant.COOKIE_DOMAIN);
                }
                break;
            }
            case 1:
            case 3:{
                customerId = CookieUtils.getCookieValInteger(request, "UserID");
                CookieUtils.setCookie(response,ST_CUSTOMER_ID,requestCustomerId,SysConstant.COOKIE_DOMAIN);
                break;
            }
            default:{
                if(!StringUtils.isEmpty(requestCustomerId)){
                    customerId = Integer.valueOf(requestCustomerId);
                    if (customerId <= 1 && environment.acceptsProfiles("development")) {
                        customerId = 4421;
                    }
                }else if (environment.acceptsProfiles("development")) {
                    customerId = 4421;
                }
            }
        }
        funcAuthorize = CookieUtils.getCookieVal(request, "MM_FuncAuthorize");
        request.setAttribute("customerId", customerId);
        request.setAttribute("loginType", loginType);
        request.setAttribute("funAuthorize", funcAuthorize);
        return true;
    }


}
