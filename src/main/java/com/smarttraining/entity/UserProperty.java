package com.smarttraining.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
public class UserProperty extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="owner_id", nullable=true)
    private User owner;
    
    private String name;
    
    private String value;
}
