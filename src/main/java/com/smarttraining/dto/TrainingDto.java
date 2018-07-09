package com.smarttraining.dto;

import com.smarttraining.dto.type.TrainingType;

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
    TrainingType type;
    int limitation;
    int attendeeCount;
}
