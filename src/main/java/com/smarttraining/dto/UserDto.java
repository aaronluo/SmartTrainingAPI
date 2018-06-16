package com.smarttraining.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class UserDto {
    Long id;
    String username;
    String password;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createDate;
    
    List<TrainingAccountDto> trainingAccounts;
    List<RoleDto> roles;
    List<UserPropertyDto> properties;
}
