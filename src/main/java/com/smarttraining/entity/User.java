package com.smarttraining.entity;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
public class User extends BaseEntity {
    
    private String username;
    
    private String password;
    
    @OneToMany(mappedBy="owner", orphanRemoval=true, cascade = CascadeType.ALL)
    private Collection<UserProperty> properties;
    
    public void addProperty(UserProperty property) {
        this.properties.add(property);
        property.setOwner(this);
    }
    
    public void removeProperty(UserProperty proerty) {
        properties.remove(proerty);
        proerty.setOwner(null);
    }
}
