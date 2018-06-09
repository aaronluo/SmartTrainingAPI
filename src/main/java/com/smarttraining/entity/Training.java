package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_training")
@SQLDelete(sql="UPDATE t_training SET active=false WHERE id=?")
@Where(clause="active = true")
public class Training extends BaseEntity {

    @Column(nullable=false)
    private String title;
    
    @Column(nullable = true, columnDefinition = "TEXT", length = 65535)
    @Lob
    private String description;
    
    @Column(precision=5, scale=2, nullable=false)
    private BigDecimal unitPrice;
    
    @Column(nullable=true)
    private Date startDate;
    
    @Column(nullable=true)
    private Date endDate;
    
    @Column(columnDefinition="int default 0")
    private int limitation;
    
    @OneToMany(mappedBy="training", cascade = CascadeType.ALL)
    @Where(clause="active=true")
    @OrderBy("id desc")
    private Collection<UserTrainingAccount> accounts = new ArrayList<UserTrainingAccount>();
}