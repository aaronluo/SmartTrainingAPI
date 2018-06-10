package com.smarttraining.exception;

@SuppressWarnings("serial")
public class InvalidUserOrPasswordException extends Exception {

    private String username;
    
    public InvalidUserOrPasswordException(String username) {
        this.username = username;
    }
    
    public String getMessage() {
        return String.format("The password does not match the user[%s]", username);
    }
}
