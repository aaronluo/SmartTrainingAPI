package com.smarttraining.exception;

@SuppressWarnings("serial")
public class UserNotFoundException extends Exception{
    private String username;
    
    public UserNotFoundException(String username) {
        this.username = username;
    }
    
    public String getMessage() {
        return String.format("The user[%s] not found!", username);
    }
}
