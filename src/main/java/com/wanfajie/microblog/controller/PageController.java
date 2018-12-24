package com.wanfajie.microblog.controller;

import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.controller.ajax.MicroBlogController;
import com.wanfajie.microblog.controller.ajax.UserController;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.util.PageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class PageController {

    @Resource
    private MicroBlogController mbController;

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

    @GetMapping("/home.html")
    public String homePage(Model model) {
        AjaxSingleResult<Map<String, Object>> result = mbController.getAllMicroBlog(1, 20, 0);
        Map<String, Object> data = result.getData();
        PageUtil.copyToModel(data, model);

        return "homepage";
    }
}
