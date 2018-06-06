package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_user_property")
@SQLDelete(sql ="update t_user_property set active=false where id = ?")
@Where(clause="active=1")
public class UserProperty extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="owner_id", nullable=true)
    private User owner;
    
    private String name;
    
    private String value;
}
