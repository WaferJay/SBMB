package com.example.demo.iinterceptor;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SessionInterceptor extends HandlerInterceptorAdapter implements SessionCookieService {

    private ThreadLocal<HttpSession> threadLocalSession = new ThreadLocal<>();
    private ThreadLocal<Map<String, Cookie>> threadLocalCookie = new ThreadLocal<>();
    private ThreadLocal<HttpServletResponse> threadLocalResp= new ThreadLocal<>();

    private Map<String, Cookie> getCookies(HttpServletRequest request) {
        Map<String, Cookie> cookies = new HashMap<>();

        if (request.getCookies() == null) {
            return cookies;
        }

        for (Cookie cookie : request.getCookies()) {
            cookies.put(cookie.getName(), cookie);
        }

        cookies = Collections.unmodifiableMap(cookies);

        return cookies;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        threadLocalSession.set(request.getSession());

        // XXX: 在调用getCurrentCookie时构造Cookie的Map
        threadLocalCookie.set(getCookies(request));
        threadLocalResp.set(response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        threadLocalSession.remove();
        threadLocalCookie.remove();
        threadLocalResp.remove();
    }

    @Override
    public HttpSession getCurrentSession() {
        return threadLocalSession.get();
    }

    @Override
    public Map<String, Cookie> getCurrentCookie() {
        return threadLocalCookie.get();
    }

    @Override
    public void addCookie(Cookie cookie) {
        threadLocalResp.get()
                .addCookie(cookie);
    }

    @Override
    public void removeCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);

        threadLocalResp.get()
                .addCookie(cookie);
    }
}
