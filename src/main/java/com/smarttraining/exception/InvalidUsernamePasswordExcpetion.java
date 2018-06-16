package com.smarttraining.exception;

@SuppressWarnings("serial")
public class InvalidUsernamePasswordExcpetion extends Exception {

    private String username;
    
    private String password;
    
    public InvalidUsernamePasswordExcpetion(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getMessage() {
        return String.format("Invalid username[%s] and/or password[%s]", username, password);
    }
}
