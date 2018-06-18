package com.smarttraining.exception;

@SuppressWarnings("serial")
public class QueryModelException extends Exception {

    String errorMsg;
    
    public QueryModelException(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public String getMessage() {
        return errorMsg;
    }
}
