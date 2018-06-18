package com.smarttraining.querymodel;

import com.smarttraining.util.RangeVal;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public final class UserQueryModel extends BaseQueryModel {

    private String username;
    
    private List<String> roles;
    
    private RangeVal<LocalDateTime> createDate;
}
