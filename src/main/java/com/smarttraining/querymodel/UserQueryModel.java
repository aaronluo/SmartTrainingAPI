package com.smarttraining.querymodel;

import java.util.Date;

import lombok.Data;

@Data
public class UserQueryModel {

    private String username;
    
    private Date createFromDate;
    
    private Date createEndDate;
}
