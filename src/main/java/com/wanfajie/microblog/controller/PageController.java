package com.wanfajie.microblog.controller;

import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.controller.ajax.MicroBlogController;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.MicroBlogService;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.PageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class PageController {

    @Resource
    private MicroBlogController mbController;

    @Resource
    private UserService userService;

    @Resource
    private MicroBlogService mbService;

    @GetMapping("/signup.html")
    public String signUpPage(Model model) {

        model.addAttribute("title", "注册账户");
        model.addAttribute("formType", "signup-form");
        return "base/form_base";
    }

    @GetMapping("/login.html")
    public String loginPage(Model model) {
        User currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            return "redirect:/home.html";
        }
        model.addAttribute("title", "用户登录");
        model.addAttribute("formType", "login-form");
        return "base/form_base";
    }

    @GetMapping("/home.html")
    @LoginRequired(redirect = true)
    public String homePage(Model model, @RequestParam(value = "mb_type", defaultValue = "all") String type) {
        AjaxSingleResult<Map<String, Object>> result;
        if (type.equals("sub")) {
            result = mbController.getSubMicroBlog(1, 10, 0);
        } else {
            result = mbController.getAllMicroBlog(1, 10, 0);
        }
        Map<String, Object> data = result.getData();
        PageUtil.copyToModel(data, model);

        return "homepage";
    }

    @GetMapping("/u/{userId}.html")
    public String userPage(Model model, @PathVariable("userId") long userId) {
        User user = userService.findById(userId);
        User currentUser = userService.getCurrentUser();

        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "没有这个用户");
        }

        AjaxSingleResult<Map<String, Object>> result = (AjaxSingleResult<Map<String, Object>>) mbController.getUserMicroBlog(1, 10, user.getId());
        Map<String, Object> data = result.getData();
        PageUtil.copyToModel(data, model);
        model.addAttribute("user", user);
        model.addAttribute("subscribed", currentUser != null &&
                userService.isFollowing(currentUser, user));
        return "userpage";
    }

    @GetMapping("/blog/{id}")
    @ResponseBody
    public String blogDetailPage() {
        return "";
    }
}
