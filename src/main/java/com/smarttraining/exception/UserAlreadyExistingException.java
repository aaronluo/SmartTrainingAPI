package com.smarttraining.exception;

@SuppressWarnings("serial")
public class UserAlreadyExistingException extends Exception {
    private String username;
    
    public UserAlreadyExistingException(String username) {
        this.username = username;
    }
    
    public String getMessage() {
        return String.format("The user[%s] has already existed.", username);
    }
}
