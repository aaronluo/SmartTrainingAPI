package com.smarttraining.exception;

@SuppressWarnings("serial")
public class TrainingAccountAlreadyExistingExecption extends Exception {

    String  username;
    String trainingName;

    public TrainingAccountAlreadyExistingExecption(String  username,
            String trainingName) {
        this.username = username;
        this.trainingName = trainingName;
    }

    public String getMessage() {
        return String.format("The user[%s] has have the account for training[%s]" , username, trainingName);
    }
}
