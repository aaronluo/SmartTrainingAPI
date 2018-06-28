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
public class DepositeQueryModel extends BaseQueryModel {

    private RangeVal<BigDecimal> amount;

    @JsonCreator
    public DepositeQueryModel(
            @JsonProperty("page") int page, 
            @JsonProperty("size") int size, 
            @JsonProperty("createDateMin") LocalDateTime createDateMin, 
            @JsonProperty("createDateMax") LocalDateTime createDateMax,
            @JsonProperty("amount") RangeVal<BigDecimal> amount) {
        
        super(page, size, createDateMin, createDateMax);
        
        this.amount = amount;

    }
}
