package com.smarttraining.querymodel;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public final class UserQueryModel {

    private String username;
    
    private LocalDateTime createFromDate;
    
    private LocalDateTime createEndDate;
}
