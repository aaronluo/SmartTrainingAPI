package com.smarttraining.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class ApiException extends Exception {
    protected ApiError apiError;
    protected Exception originalException;
    
    public ApiException(ApiError apiError, Exception originalException) {
        this.apiError = apiError;
        this.originalException = originalException;
    }
    
    public Exception getOriginalException(){
        return this.originalException;
    }
    
    public HttpStatus getHttpStatus(){
        return this.apiError.getStatus();
    }
    
    public ApiError getApiError(){
        return this.apiError;
    }
    
    @Override
    public String getMessage(){
        return this.apiError.getErrMsg();
    }
}
