package com.smarttraining.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import lombok.Data;

@Data
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity{

    @Id
    @GeneratedValue
    protected Long id;
    
    @Column(columnDefinition="boolean default true")
	protected boolean active = true;
    
//    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createDate;
    
//    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime updateDate;
    
    @PreRemove
    public void inactivate() {
        this.active = false;
    }
    
    @PrePersist
    public void prePersist() {
        createDate = LocalDateTime.now(ZoneId.systemDefault());
        updateDate = createDate;
    }
    
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now(ZoneId.systemDefault());
    }
}
