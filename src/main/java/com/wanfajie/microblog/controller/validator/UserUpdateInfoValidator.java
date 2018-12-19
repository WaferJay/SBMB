package com.wanfajie.microblog.controller.validator;

import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.UserUpdateInfoForm;
import com.wanfajie.microblog.service.UserService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserUpdateInfoValidator implements Validator {

    private static UserUpdateInfoValidator instance;

    private UserService userService;

    private UserUpdateInfoValidator(UserService service) {
        userService = service;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserUpdateInfoForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserUpdateInfoForm form = (UserUpdateInfoForm) o;
        User currentUser = userService.getCurrentUser();

        String name = form.getName();
        String email = form.getEmail();

        if (name != null) {
            User user = userService.findByName(name);

            if (user != null && !user.equals(currentUser)) {
                errors.rejectValue("name", "用户名已被使用");
            }
        }

        if (email != null) {
            User user = userService.findByEmail(email);

            if (user != null && !user.equals(currentUser)) {
                errors.rejectValue("email", "该邮箱已注册");
            }
        }
    }

    public static UserUpdateInfoValidator getInstance(UserService userService) {
        if (instance == null) {

            synchronized (UserUpdateInfoValidator.class) {
                if (instance == null) {
                    instance = new UserUpdateInfoValidator(userService);
                }
            }
        }

        return instance;
    }
}
