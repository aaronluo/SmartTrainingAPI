package com.smarttraining.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    protected Long id;
    
    @Column(columnDefinition="boolean default true")
	protected boolean active;
    
    protected Date createDate;
    
    protected Date updateDate;
    
//	public Date getCreateDate() {
//        return createDate;
//    }
//
//    public void setCreateDate(Date createDate) {
//        this.createDate = createDate;
//    }
//
//    public Date getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(Date updateDate) {
//        this.updateDate = updateDate;
//    }
//
//    public boolean isActive() {
//		return active;
//	}
//
//	public void setActive(boolean active) {
//		this.active = active;
//	}
//	
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//    
//	public BaseEntity() {
//		this.active = true;
//		this.createDate = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
//		this.updateDate = this.createDate;
//	}
}
