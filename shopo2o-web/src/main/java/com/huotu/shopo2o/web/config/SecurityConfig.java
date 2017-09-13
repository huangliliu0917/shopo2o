package com.huotu.shopo2o.web.config;

import com.huotu.shopo2o.web.config.security.AuthenticationProvider;
import com.huotu.shopo2o.web.config.security.filter.AuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by hxh on 2017-08-31.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String loginPage = "/login";
    private static final String loginSuccessURL = "/loginSuccess";
    private static final String loginFailedURL = "/loginFailed";
    private static final String logoutSuccessURL = "/";
    private static String[] STATIC_RESOURCE_PATH = {
            "/resources/**",
            "/loginFailed",
            "/code/verifyImage"
    };
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationProcessingFilter authenticationProcessingFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationProvider();
    }

    @Bean
    public AuthenticationProcessingFilter authenticationProcessingFilter() throws Exception {
        AuthenticationProcessingFilter authenticationProcessingFilter = new AuthenticationProcessingFilter();
        authenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        authenticationProcessingFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler(loginSuccessURL));
        authenticationProcessingFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(loginFailedURL));
        return authenticationProcessingFilter;
    }

    //设置拦截规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin().and()
                .addFilterBefore(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .loginPage(loginPage)
                .defaultSuccessUrl(loginSuccessURL)
                .failureUrl(loginFailedURL)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl(logoutSuccessURL);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(STATIC_RESOURCE_PATH);
    }
}
