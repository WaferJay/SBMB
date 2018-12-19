package com.wanfajie.microblog.bean.form;

import com.wanfajie.microblog.bean.User;

public class SignUpForm {

    private String name;
    private String password;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void transcriptUser(User user) {
        user.setName(getName());
        user.setEmail(getEmail());
        user.setPassword(getPassword());
    }
}
