package com.huotu.shopo2o.web.config;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.service.config.ServiceConfig;
import com.huotu.shopo2o.web.config.security.LoginUserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hxh on 2017-08-31.
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan({
        "com.huotu.shopo2o.web.controller"
        , "com.huotu.shopo2o.web.service"
})
@Import({MVCConfig.ThymeleafConfig.class, ServiceConfig.class, SecurityConfig.class})
public class MVCConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    //只是为了初始化
    @Autowired
    private SysConstant sysConstant;

    /**
     * for upload
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        converters.add(converter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/resources/**/*","image/**/*", "/**/*.html")
                .addResourceLocations("/resources/","/image/", "/");
    }

    /**
     * 国际化设置
     *
     * @return
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("message/message");
        return messageSource;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafViewResolver);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginUserResolver());
    }

    @Configuration
    public static class ThymeleafConfig {
        @Autowired
        private ApplicationContext applicationContext;
        @Autowired
        private Environment env;

        @Bean
        @SuppressWarnings("Duplicates")
        public SpringResourceTemplateResolver templateResolver() {
            // SpringResourceTemplateResolver automatically integrates with Spring's own
            // resource resolution infrastructure, which is highly recommended.
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setApplicationContext(this.applicationContext);
            templateResolver.setPrefix("/views/");
            templateResolver.setSuffix(".html");
            // HTML is the default value, added here for the sake of clarity.
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setCharacterEncoding("UTF-8");
            // Template cache is true by default. Set to false if you want
            // templates to be automatically updated when modified.
            if (env.acceptsProfiles("!container")) {
                templateResolver.setCacheable(false);
            } else {
                templateResolver.setCacheable(true);
            }
            return templateResolver;
        }

        @Bean
        public SpringTemplateEngine templateEngine() {
            // SpringTemplateEngine automatically applies SpringStandardDialect and
            // enables Spring's own MessageSource message resolution mechanisms.
            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.addDialect(new SpringSecurityDialect());
            templateEngine.addDialect(new Java8TimeDialect());
            templateEngine.setTemplateResolver(templateResolver());
            return templateEngine;
        }

        @Bean(name = "thymeleafViewResolver")
        @SuppressWarnings("Duplicates")
        public ThymeleafViewResolver viewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(templateEngine());
            // NOTE 'order' and 'viewNames' are optional
            viewResolver.setCharacterEncoding("UTF-8");
            viewResolver.setContentType("text/html;charset=utf-8");
            return viewResolver;
        }
    }
}
