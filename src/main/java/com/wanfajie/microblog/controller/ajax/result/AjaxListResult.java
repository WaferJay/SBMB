package com.wanfajie.microblog.controller.ajax.result;

import java.util.ArrayList;
import java.util.List;

public class AjaxListResult<T> extends AjaxResult {

    private List<? extends T> items;

    public AjaxListResult(int code, String message, List<? extends T> items) {
        super(code, message);
        this.items = items;
    }

    public AjaxListResult(int code, String message) {
        this(code, message, new ArrayList<>());
    }

    public List<? extends T> getItems() {
        return items;
    }

    public void setItems(List<? extends T> items) {
        this.items = items;
    }

}
