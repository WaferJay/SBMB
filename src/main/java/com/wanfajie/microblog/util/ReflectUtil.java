package com.wanfajie.microblog.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtil {

    private ReflectUtil() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Object> convertInterfaceToMap(Class ifClass) {
        if (!ifClass.isInterface()) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        for (Field field : ifClass.getFields()) {

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            String key = field.getName();
            Object value = ReflectionUtils.getField(field, null);

            result.put(key, value);
        }

        for (Class aClass : ifClass.getClasses()) {
            String key = aClass.getSimpleName();
            Map<String, Object> value = convertInterfaceToMap(aClass);

            result.put(key, value);
        }


        return result;
    }
}
