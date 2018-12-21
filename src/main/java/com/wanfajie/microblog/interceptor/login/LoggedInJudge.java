package com.wanfajie.microblog.interceptor.login;

import javax.servlet.http.HttpServletRequest;

public interface LoggedInJudge {
    boolean isLogin(HttpServletRequest request);
}
