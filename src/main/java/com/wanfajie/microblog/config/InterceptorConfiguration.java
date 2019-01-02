package com.wanfajie.microblog.config;

import com.wanfajie.microblog.interceptor.SessionInterceptor;
import com.wanfajie.microblog.interceptor.login.LoginInterceptor;
import com.wanfajie.microblog.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurationSupport {

    @Resource
    private SessionInterceptor sessionInterceptor;

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private UserService userService;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");

        loginInterceptor.setJudge(request -> userService.getCurrentUser() != null);
        loginInterceptor.setLoginUrl("/login.html");

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**");
    }
}
