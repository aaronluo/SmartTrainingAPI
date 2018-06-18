package com.smarttraining.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TrainingAccountDto {
    Long ownerId;
    String ownerUsername;
    TrainingDto training;
    BigDecimal balance;
    BigDecimal unitPrice;
    LocalDate validBeginDate;
    LocalDate validEndDate;
}
