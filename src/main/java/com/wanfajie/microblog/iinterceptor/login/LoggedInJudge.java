package com.wanfajie.microblog.iinterceptor.login;

import javax.servlet.http.HttpServletRequest;

public interface LoggedInJudge {
    boolean isLogin(HttpServletRequest request);
}
