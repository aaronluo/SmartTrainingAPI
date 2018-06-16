package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    
    
    @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(name="t_user_role",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="role_id")})
    @Where(clause="active=true")
    @OrderBy("id desc")
    private Collection<Role> roles = new ArrayList<Role>();
    
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
    
    public void addRole(Role role) {
        roles.add(role);
//        role.getUsers().add(this);
    }
}
