package com.huotu.shopo2o.web;


import com.gargoylesoftware.htmlunit.WebClient;
import com.huotu.shopo2o.web.filter.ParamFilter;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

/**
 * Created by helloztt on 2017-06-28.
 */
public class SpringWebTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;
    /**
     * 选配 只有在SecurityConfig起作用的情况下
     **/
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    private FilterChainProxy springSecurityFilter;

    /**
     * mock请求
     **/
    @Autowired
    protected MockHttpServletRequest request;

    protected MockMvc mockMvc;
    protected WebClient webClient;
    protected WebDriver webDriver;
    protected ParamFilter paramFilter = new ParamFilter();

    @Before
    public void setup() {
        if (springSecurityFilter != null)
            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                    .addFilters(springSecurityFilter)
                    .build();
        else
            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                    .addFilter(paramFilter)
                    .build();
        this.webClient = MockMvcWebClientBuilder
                .mockMvcSetup(this.mockMvc)
                .build();
        this.webDriver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(this.mockMvc)
                .build();
    }

    @After
    public void afterTest() {
        if (webDriver != null) {
            webDriver.close();
        }
    }
    /**
     * 保存登陆过以后的信息
     **/
    protected void saveAuthedSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext == null)
            throw new IllegalStateException("尚未登录");

        request.setSession(session);

        // context 不为空 表示成功登陆
        SecurityContextHolder.setContext(securityContext);
    }
}
