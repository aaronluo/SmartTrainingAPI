package com.smarttraining.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrainingLogDto {

    Long id;
    Long trainerId;
    Long traineeId;
    Long  accountId;
    String accountTrainingTitle;
    LocalDateTime createDate;
    String comment;
}
