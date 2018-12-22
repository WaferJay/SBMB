package com.wanfajie.microblog.bean.form;

import javax.validation.constraints.NotEmpty;

public class UserLoginForm {

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotEmpty(message = "请填写密码")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
