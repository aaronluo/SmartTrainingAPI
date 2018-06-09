package com.smarttraining.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    protected LocalDateTime createDate;
    
    @LastModifiedDate
    protected LocalDateTime updateDate;
    
    @PreRemove
    public void inactivate() {
        this.active = false;
    }
}
