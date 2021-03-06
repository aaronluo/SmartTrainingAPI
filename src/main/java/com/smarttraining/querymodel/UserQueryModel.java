package com.smarttraining.querymodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class UserQueryModel extends BaseQueryModel {

    private String username;
    private List<String> roles;

    @JsonCreator
    public UserQueryModel(
            @JsonProperty("username") String username,
            @JsonProperty("roles") List<String> roles,
            @JsonProperty("page") int page, 
            @JsonProperty("size") int size, 
            @JsonProperty("createDateMin") LocalDateTime createDateMin, 
            @JsonProperty("createDateMax") LocalDateTime createDateMax) {

        super(page, size, createDateMin, createDateMax);

        this.username = username;
        this.roles = roles;
    }
}
