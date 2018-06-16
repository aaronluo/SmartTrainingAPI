package com.smarttraining.exception;

@SuppressWarnings("serial")
public class TokenMissingException extends Exception {

    public String getMessage() {
        return "Authen Token is missing";
    }
}
