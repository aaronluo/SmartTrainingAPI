package com.smarttraining.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrainingLogDto {

    Long trainerId;
    Long traineeId;
    Long  accountId;
    LocalDateTime createDate;
    String comment;
}
