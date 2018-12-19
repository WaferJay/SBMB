package com.example.demo.controller.validator;

import com.example.demo.bean.form.UserLoginForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginFormValidator implements Validator {

    private static LoginFormValidator instance;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserLoginForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "用户名必须填写");
        ValidationUtils.rejectIfEmpty(errors, "password", "密码必须填写");
    }

    public static LoginFormValidator getInstance() {

        if (instance == null) {

            synchronized (LoginFormValidator.class) {
                if (instance == null) instance = new LoginFormValidator();
            }
        }

        return instance;
    }
}
