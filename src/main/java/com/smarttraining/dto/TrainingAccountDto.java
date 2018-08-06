package com.smarttraining.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingAccountDto {
    Long id;
//    Long ownerId;
//    String ownerUsername;
    TrainingDto training;
//    Long trainingId;
//    String trainingTitle;
    BigDecimal balance;
    BigDecimal unitPrice;
    LocalDate validBeginDate;
    LocalDate validEndDate;
    int depositeLogCount;
    int trainingLogCount;
}
