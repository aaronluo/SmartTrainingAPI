package com.smarttraining.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TrainingDto {

    Long id;
    String title;
    String description;
    BigDecimal unitPrice;
    LocalDate startDate;
    LocalDate endDate;
    int limitation;
}
