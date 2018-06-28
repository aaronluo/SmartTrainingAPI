package com.smarttraining.querymodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.smarttraining.util.RangeVal;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public abstract class BaseQueryModel {

    protected int page;
    protected int size;
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateMin;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateMax;
    
    @JsonIgnore
    private RangeVal<LocalDateTime> createDate;
    
    public BaseQueryModel(int page,  int size,  LocalDateTime createDateMin,  LocalDateTime createDateMax) {
        this.page = page;
        this.size = size;
        this.createDate = new RangeVal<LocalDateTime>(createDateMin, createDateMax);
    }
    
    public BaseQueryModel() {
        this.page = 1;
        this.size = 10;
    }
}
