package com.example.demo.config;

import com.example.demo.iinterceptor.SessionInterceptor;
import com.example.demo.iinterceptor.login.LoginInterceptor;
import com.example.demo.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");


        loginInterceptor.setJudge(request -> userService.getCurrentUser() != null);

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**");
    }
}
