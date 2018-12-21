package com.wanfajie.microblog.controller.ajax;

import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.SignUpForm;
import com.wanfajie.microblog.bean.form.UserLoginForm;
import com.wanfajie.microblog.bean.form.UserUpdateInfoForm;
import com.wanfajie.microblog.controller.ajax.result.AjaxListResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.controller.validator.LoginFormValidator;
import com.wanfajie.microblog.controller.validator.UserSignUpValidator;
import com.wanfajie.microblog.controller.validator.UserUpdateInfoValidator;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.PasswordUtil;
import com.wanfajie.microblog.util.ValidUtil;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserService userService;

    @RequestMapping(AjaxURLConfig.User.USER_PING)
    public AjaxResult ping() {
        User user = userService.getCurrentUser();

        Map<String, User> data = Collections.singletonMap("current_user", user);
        return new AjaxSingleResult<>(0, "请求成功", data);
    }

    @PutMapping(AjaxURLConfig.User.USER_REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public AjaxResult registerUser(@RequestBody SignUpForm form, BindingResult bindingResult) {
        UserSignUpValidator.getInstance(em)
                .validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            List<Map<String, Object>> errors = ValidUtil.convertFieldErrors(bindingResult.getFieldErrors());

            return new AjaxListResult<>(1, "表单错误", errors);
        }

        User user = new User();
        form.transcriptUser(user);
        String cipher = PasswordUtil.encryptPwd(user.getPassword());
        user.setPassword(cipher);
        userService.save(user);

        AjaxSingleResult<Map<String, String>> result = new AjaxSingleResult<>(0, "注册成功");
        result.setData(Collections.singletonMap("redirect", "/user_page.html"));
        return result;
    }

    @RequestMapping(value = AjaxURLConfig.User.USER_INFO, method = RequestMethod.GET)
    public AjaxSingleResult<User> userInfo(@PathVariable("id") long id) {
        AjaxSingleResult<User> result = new AjaxSingleResult<>();

        User user = userService.findById(id);
        if (user == null) {
            result.setCode(1);
            result.setMessage("没有该用户");
        } else {
            result.setCode(0);
            result.setMessage("成功");
        }
        result.setData(user);

        return result;
    }

    @RequestMapping(value = AjaxURLConfig.User.USER_INFO, method = RequestMethod.POST)
    @LoginRequired(redirect = false)
    @Transactional
    public AjaxResult updateUserInfo(@PathVariable("id") long id, @RequestBody UserUpdateInfoForm form, BindingResult bindingResult) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getId() != id) {
            return new AjaxResult(400, "没有权限修改其他用户的资料");
        }

        UserUpdateInfoValidator.getInstance(userService)
                .validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            List<Map<String, Object>> errors = ValidUtil.convertFieldErrors(fieldErrors);

            AjaxListResult<Map<String, Object>> result = new AjaxListResult<>(2, "表单错误");
            result.setItems(errors);

            return result;
        }

        String name = form.getName();
        String password = form.getPassword();
        String email = form.getEmail();

        if (name != null) currentUser.setName(name);
        if (email != null) currentUser.setEmail(email);
        if (password != null) {
            currentUser.setPassword(PasswordUtil.encryptPwd(password));
        }
        em.merge(currentUser);

        return new AjaxResult(0, "资料修改成功");
    }

    @RequestMapping(value = AjaxURLConfig.User.USER_LOGIN, method = RequestMethod.POST)
    public AjaxResult login(@RequestBody UserLoginForm form, BindingResult bindingResult) {
        User currentUser = userService.getCurrentUser();

        if (currentUser != null) {
            return new AjaxSingleResult<>(0, "已登录状态", currentUser);
        }

        LoginFormValidator.getInstance()
                .validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            List<Map<String, Object>> errors = ValidUtil.convertFieldErrors(bindingResult.getFieldErrors());
            return new AjaxListResult<>(1,  "表单错误", errors);
        }

        User user = userService.findByName(form.getUsername());

        if (user == null) {
            return new AjaxResult(2, "用户或密码错误");
        }

        boolean v = PasswordUtil.verifyPassword(form.getPassword(), user.getPassword());

        if (v) {
            userService.login(user, false);

            Map<String, Object> data = new HashMap<>();
            data.put("location", "/");
            data.put("user_info", user);
            return new AjaxSingleResult<>(0, "登录成功", data);
        }

        return new AjaxResult(2, "用户或密码错误");
    }

    @RequestMapping(value = AjaxURLConfig.User.USER_LOGOUT, method = RequestMethod.POST)
    public AjaxResult logout () {
        if (userService.getCurrentUser() != null) {
            userService.logout();
        }

        return new AjaxResult(0, "成功");
    }
}
