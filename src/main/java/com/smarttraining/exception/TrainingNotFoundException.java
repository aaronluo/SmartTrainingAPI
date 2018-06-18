package com.smarttraining.exception;

@SuppressWarnings("serial")
public class TrainingNotFoundException extends Exception {

    long trainingId;
    
    public TrainingNotFoundException(long trainingId) {
        this.trainingId = trainingId;
    }
    
    public String getMessage() {
        return String.format("The training[%d] not found.",trainingId);
    }
}
