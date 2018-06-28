package com.smarttraining.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

@SuppressWarnings("serial")
public class RangeValDeserializer<T> extends StdDeserializer<RangeVal<T>> {

    protected RangeValDeserializer(Class<?> vc) {
        super(vc);
    }

    
    @Override
    public RangeVal<T> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
