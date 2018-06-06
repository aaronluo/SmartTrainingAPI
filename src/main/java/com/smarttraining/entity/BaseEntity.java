package com.smarttraining.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreRemove;

import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity{

    @Id
    @GeneratedValue
    protected Long id;
    
    @Column(columnDefinition="boolean default true")
	protected boolean active = true;
    
    @CreatedDate
    protected Date createDate;
    
    @LastModifiedDate
    protected Date updateDate;
    
    @PreRemove
    public void inactivate() {
        this.active = false;
    }
}
