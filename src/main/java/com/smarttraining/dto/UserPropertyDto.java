package com.smarttraining.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPropertyDto {

    Long id;
    String name;
    String value;
    
}
