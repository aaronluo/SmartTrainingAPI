package com.smarttraining.exception;

import org.springframework.http.HttpStatus;

public class ApiError {
    protected HttpStatus status;
    protected String errMsg;
    
    public HttpStatus getStatus() {
        return status;
    }
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
    public String getErrMsg() {
        return errMsg;
    }
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    public ApiError(HttpStatus status, String errMsg) {
        this();
        this.status = status;
        this.errMsg = errMsg;
    }
    public ApiError() {
        this.status = HttpStatus.OK;
        this.errMsg = "";
    }
}
