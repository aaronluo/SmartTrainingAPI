package com.smarttraining.exception;

@SuppressWarnings("serial")
public class PropertyAlreadyExistingException extends Exception {
    
    private String username;
    private String propName;
    
    public PropertyAlreadyExistingException(String username, String propName) {
        this.username = username;
        this.propName = propName;
    } 
    
    public String getMessage() {
        return String.format("The property[%s] of the user[%s] already there", propName, username);
    }
}
