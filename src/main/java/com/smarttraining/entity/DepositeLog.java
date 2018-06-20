package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 充值记录
 * @author luoz
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_deposite_log")
@SQLDelete(sql="UPDATE t_deposite_log SET active=false WHERE id=?")
@Where(clause="active = true")
public class DepositeLog extends BaseEntity{

    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="account_id")
    private TrainingAccount account;
    
    @Column(nullable=false)
    private BigDecimal amount = new BigDecimal(0.00);
    
    @Column(nullable=true)
    private String comment;
    
}
