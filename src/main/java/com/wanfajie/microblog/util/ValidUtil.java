package com.wanfajie.microblog.util;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidUtil {

    private static final Pattern P_EMAIL = Pattern.compile("^\\w+@\\w+(?:\\.\\w+)*$");

    private ValidUtil() {
        throw new UnsupportedOperationException();
    }

    public static List<Map<String, Object>> convertFieldErrors(List<FieldError> fieldErrors) {

        List<Map<String, Object>> result = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String code = fieldError.getCode();
            Object value = fieldError.getRejectedValue();

            Map<String, Object> error = new HashMap<>();
            error.put("field", field);
            error.put("code", code);
            error.put("value", value);
            error.put("message", fieldError.getDefaultMessage());

            result.add(error);
        }

        return result;
    }

    public static boolean isEmailAddress(String email) {
        return P_EMAIL.matcher(email).matches();
    }
}
