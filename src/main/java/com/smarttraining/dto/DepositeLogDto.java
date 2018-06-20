package com.smarttraining.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DepositeLogDto {

    TrainingAccountDto account;
    
    BigDecimal amount;
    
    LocalDateTime createDate;
    
    String comment;
}
