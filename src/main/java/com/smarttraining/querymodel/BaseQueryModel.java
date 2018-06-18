package com.smarttraining.querymodel;

import lombok.Data;

@Data
public abstract class BaseQueryModel {

    protected int page;
    protected int size;
}
