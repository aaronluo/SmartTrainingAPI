package com.smarttraining.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserToken {

    private String username;
    
    private Long userId;
    
    private String accessToken;
    
    private String refreshToken;
    
    private LocalDateTime expireDatetime;
}
