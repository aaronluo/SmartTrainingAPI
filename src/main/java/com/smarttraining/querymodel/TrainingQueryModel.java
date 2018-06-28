package com.smarttraining.querymodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraining.util.RangeVal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class TrainingQueryModel extends BaseQueryModel {
    private String title;
    
    private RangeVal<BigDecimal> unitPrice;
    
    private RangeVal<Integer> limitation;
    
    public TrainingQueryModel()  {
        super();
    }
    @JsonCreator
    public TrainingQueryModel(
            @JsonProperty("title") String title,
            @JsonProperty("unitPrice") RangeVal<BigDecimal> unitPrice,
            @JsonProperty("limitation") RangeVal<Integer> limitation,
            @JsonProperty("page") int page, 
            @JsonProperty("size") int size, 
            @JsonProperty("createDateMin") LocalDateTime createDateMin, 
            @JsonProperty("createDateMax") LocalDateTime createDateMax) {
    
        super(page, size, createDateMin, createDateMax);
        
        this.title = title;
        this.unitPrice = unitPrice;
        this.limitation = limitation;
    }
}
