package com.example.demo.controller.ajax.result;

public class AjaxSingleResult<T> extends AjaxResult {
    private T data;

    public AjaxSingleResult() {}

    public AjaxSingleResult(int code, String message) {
        super(code, message);
    }

    public AjaxSingleResult(int code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
