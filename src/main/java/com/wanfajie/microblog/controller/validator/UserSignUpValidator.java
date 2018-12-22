package com.wanfajie.microblog.controller.validator;

import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.SignUpForm;
import com.wanfajie.microblog.util.ValidUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.regex.Pattern;

public class UserSignUpValidator implements Validator {

    private static UserSignUpValidator instance;

    private EntityManager manager;

    public UserSignUpValidator(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SignUpForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignUpForm form = (SignUpForm) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "此项必须填写");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "此项必须填写");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "此项必须填写");

        if (form.getEmail() != null && !ValidUtil.isEmailAddress(form.getEmail())) {
            errors.rejectValue("email", "邮箱地址无效");
        }

        User user = findByNameOrEmail(form.getName(), form.getEmail());
        if (user != null) {

            if (user.getName().equals(form.getName())) {
                errors.rejectValue("name", "用户名已被注册");
            }

            if (user.getEmail().equals(form.getEmail())) {
                errors.rejectValue("email", "邮箱地址已被注册");
            }
        }
    }

    private User findByNameOrEmail(String name, String email) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);

        Root<User> root = query.from(User.class);
        Predicate orPre = builder.or(
            builder.equal(root.<String>get("name"), name),
            builder.equal(root.<String>get("email"), email)
        );

        query.where(orPre);

        User user;
        try {
            user = manager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return user;
    }

    public static UserSignUpValidator getInstance(EntityManager manager) {
        if (instance == null) {

            synchronized (UserSignUpValidator.class) {

                if (instance == null) {
                    instance = new UserSignUpValidator(manager);
                }
            }
        }

        return instance;
    }
}
