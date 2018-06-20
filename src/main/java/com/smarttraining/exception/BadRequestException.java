package com.smarttraining.exception;

@SuppressWarnings("serial")
public class BadRequestException extends Exception {

    String errorMsg;
    
    public BadRequestException(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public String getMessage() {
        return errorMsg;
    }
}
