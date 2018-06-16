package com.smarttraining.querymodel;

import com.smarttraining.util.RangeVal;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public final class UserQueryModel {

    private String username;
    
    private RangeVal<LocalDateTime> createDate;
}
