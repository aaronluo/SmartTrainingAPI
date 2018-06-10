package com.smarttraining.entity.type;

public enum EntityStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");
    
    private final String text;
    
    private EntityStatus(final String text) {
        this.text = text;
    }
    
    @Override
    public String toString(){
        return text;
    }
}
