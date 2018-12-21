package com.wanfajie.microblog.interceptor.login;

import javax.servlet.http.HttpServletRequest;

public class PassJudge implements LoggedInJudge {
    @Override
    public boolean isLogin(HttpServletRequest request) {
        return true;
    }
}
