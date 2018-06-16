package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_role")
@SQLDelete(sql ="update t_role set active=false where id = ?")
@Where(clause="active=true")
public class Role extends BaseEntity {

    public static class RoleType {
        public static String ADMIN = "ADMIN";
        public static String TRAINER = "TRAINER";
        public static String TRAINEE = "TRAINEE";
        public static String GUEST = "GUEST";
    }
    
    @Column(nullable = false)
    private String name;
    
    @ManyToMany(mappedBy="roles", fetch=FetchType.LAZY)
    private Set<User> users = new HashSet<User>();
    
    public void addUser(User user) {
        users.add(user);
    }
}
