package com.example.demo.iinterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Map;

public interface SessionCookieService {
    HttpSession getCurrentSession();
    Map<String, Cookie> getCurrentCookie();
    void addCookie(Cookie cookie);
    void removeCookie(String name);
}
