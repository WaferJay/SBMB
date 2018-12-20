package com.wanfajie.microblog.controller.ajax.result;

public class AjaxExceptionResult extends AjaxResult {

    private Exception exception;

    public AjaxExceptionResult(int code, Exception e) {
        super(code, e.getMessage());
        exception = e;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
