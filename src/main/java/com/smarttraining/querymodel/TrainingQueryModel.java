package com.smarttraining.querymodel;

import com.smarttraining.util.RangeVal;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public final class TrainingQueryModel {
    private String title;
    
    private RangeVal<BigDecimal> unitPrice;
    
    private RangeVal<LocalDate> validDate;
    
    private RangeVal<Integer> limitation;
    
}
