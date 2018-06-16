package com.smarttraining.util;

import lombok.Data;

@Data
public final class RangeVal<T> {

    private T minVal;
    private T maxVal;
    
    public RangeVal(T min, T max) {
        this.minVal = min;
        this.maxVal = max;
    }
}
