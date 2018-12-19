package com.example.demo.iinterceptor.login;

import com.example.demo.iinterceptor.login.annotation.LoginRequired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private String loginUrl = null;
    private LoggedInJudge judge = new PassJudge();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            if (method.hasMethodAnnotation(LoginRequired.class)) {

                if (judge.isLogin(request)) {

                    return true;
                }

                LoginRequired loginRequired = method.getMethodAnnotation(LoginRequired.class);

                if (loginRequired.redirect() && loginUrl != null) {
                    response.sendRedirect(loginUrl);
                } else {
                    response.sendError(403, "Forbidden");
                }

                return false;
            }
        } else {
            System.out.println(handler.getClass());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

    public void setJudge(LoggedInJudge judge) {
        this.judge = judge;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
