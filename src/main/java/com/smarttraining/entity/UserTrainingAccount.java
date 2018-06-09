package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * to store accounts information of a specific training
 * @author luoz
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_user_account")
@SQLDelete(sql="UPDATE t_user_training_account SET active=false WHERE id=?")
@Where(clause="active = true")
public class UserTrainingAccount extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name="owner_id", nullable=true)
    private User owner;
    
    @Column(precision=9, scale=2)
    private BigDecimal balance;
    
    private Date validBeginDate;
    
    private Date validEndDate;
    
    @Column(precision=5, scale=2)
    private BigDecimal unitPrice;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="training_id")
    private Training training;
}
