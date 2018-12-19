package com.wanfajie.microblog.controller.ajax;

import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.util.ReflectUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AjaxURLMapController {

    private static final Map<String, Object> config;
    private static Exception exception = null;
    private static final boolean success;

    static {
        Map<String, Object> configMap = null;
        try {
            configMap = ReflectUtil.convertInterfaceToMap(AjaxURLConfig.class);
        } catch (Exception e) {
            exception = e;
        }

        if (configMap == null) {
            success = false;
            config = null;
        } else {
            success = true;
            config = Collections.unmodifiableMap(configMap);
        }

    }

    @RequestMapping("/ajax")
    public AjaxSingleResult<Map<String, Object>> getConfig() {
        AjaxSingleResult<Map<String, Object>> result = new AjaxSingleResult<>();

        if (success) {
            result.setCode(0);
            result.setMessage("成功");
            result.setData(config);
        } else {
            result.setCode(500);
            result.setMessage("服务器错误");
            Map<String, Object> data = new HashMap<>();
            data.put("exception", exception.getClass().getTypeName());
            data.put("message", exception.getMessage());
            result.setData(data);
        }

        return result;
    }
}
