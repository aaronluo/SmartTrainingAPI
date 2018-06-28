package com.smarttraining.querymodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingLogQueryModel extends BaseQueryModel {

    Long trainerId;
    
    @JsonCreator
    public TrainingLogQueryModel(
            @JsonProperty("page") int page, 
            @JsonProperty("size") int size, 
            @JsonProperty("createDateMin") LocalDateTime createDateMin, 
            @JsonProperty("createDateMax") LocalDateTime createDateMax,
            @JsonProperty("trainerId") Long trainerId) {
        
        super(page, size, createDateMin, createDateMax);
        
        this.trainerId  = trainerId;

    }
}
