package com.wanfajie.microblog.util;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageUtil {

    private PageUtil() {
        throw new UnsupportedOperationException();
    }

    public static Map<String, Object> page2Map(Page page) {
        Map<String, Object> data = new HashMap<>();
        data.put("total_pages", page.getTotalPages());
        data.put("total_items", page.getTotalElements());
        data.put("current_page", page.getNumber() + 1);  // 当前页从0开始
        data.put("page_size", page.getSize());
        List items = page.getContent();
        data.put("items", items);
        return data;
    }
}
