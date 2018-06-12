package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_user")
@SQLDelete(sql ="update t_user set active=false where id = ?")
@Where(clause="active=true")
public class User extends BaseEntity {
    
    private String username;
    
    private String password;
    
    @OneToMany(mappedBy="owner", orphanRemoval=true, cascade = CascadeType.ALL)
    @Where(clause="active=1")
    @OrderBy("id desc")
    private Collection<UserProperty> properties = new ArrayList<UserProperty>();
    
    @OneToMany(mappedBy="owner", cascade= CascadeType.ALL)
    @Where(clause="active=true")
    @OrderBy("createDate desc")
    private Collection<TrainingAccount> trainingAccounts = new ArrayList<TrainingAccount>();
    
    public void addProperty(UserProperty property) {
        this.properties.add(property);
        property.setOwner(this);
    }
    
    public void removeProperty(UserProperty proerty) {
        properties.remove(proerty);
        proerty.setOwner(null);
    }
    
    public void addTrainingAccount(TrainingAccount account) {
        trainingAccounts.add(account);
        account.setOwner(this);
    }
}
