package com.smarttraining.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrainingLogDto {

    UserDto trainer;
    UserDto trainee;
    TrainingAccountDto  account;
    LocalDateTime createDate;
}
