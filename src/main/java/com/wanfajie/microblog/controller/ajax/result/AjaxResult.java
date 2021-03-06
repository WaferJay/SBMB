package com.wanfajie.microblog.controller.ajax.result;

public class AjaxResult {

    private int code;
    private String message;

    public AjaxResult() {}

    public AjaxResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
