package com.wanfajie.microblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/signup.html")
    public String signUpPage(Model model) {

        model.addAttribute("title", "注册账户");
        model.addAttribute("formType", "signup-form");
        return "base/form_base";
    }

    @GetMapping("/login.html")
    public String loginPage(Model model) {

        model.addAttribute("title", "用户登录");
        model.addAttribute("formType", "login-form");
        return "base/form_base";
    }
}
