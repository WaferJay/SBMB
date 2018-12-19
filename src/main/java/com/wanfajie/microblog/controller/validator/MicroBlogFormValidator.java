package com.wanfajie.microblog.controller.validator;

import com.wanfajie.microblog.bean.form.MicroBlogForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class MicroBlogFormValidator implements Validator {

    private static MicroBlogFormValidator instance;

    @Override
    public boolean supports(Class<?> aClass) {
        return MicroBlogForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MicroBlogForm form = (MicroBlogForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "微博内容不能为空");

        // TODO: 媒体文件是否存在
    }

    public static MicroBlogFormValidator getInstance() {
        if (instance == null) {

            synchronized (MicroBlogFormValidator.class) {

                if (instance == null) instance = new MicroBlogFormValidator();
            }
        }
        return instance;
    }
}
