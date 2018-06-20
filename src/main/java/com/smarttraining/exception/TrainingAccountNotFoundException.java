package com.smarttraining.exception;

@SuppressWarnings("serial")
public class TrainingAccountNotFoundException extends Exception {

    private String username;
    private Long accountId;
    
    public TrainingAccountNotFoundException(String username, Long accountId) {

        this.username = username;
        this.accountId = accountId;
    }

    public String getMessage() {
        return String.format("The user[%s] does not have the account[%d]", username, accountId);
    }
}
