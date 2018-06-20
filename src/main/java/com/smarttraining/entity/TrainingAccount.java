package com.smarttraining.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name="t_training_account")
@SQLDelete(sql="UPDATE t_training_account SET active=false WHERE id=?")
@Where(clause="active = true")
public class TrainingAccount extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name="owner_id", nullable=true)
    private User owner;
    
    @Column(precision=9, scale=2)
    private BigDecimal balance = new BigDecimal(0.00);
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable=false)
    private LocalDate validBeginDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable=true)
    private LocalDate validEndDate;
    
    @Column(precision=5, scale=2)
    private BigDecimal unitPrice;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="training_id")
    private Training training;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="account")
    private Collection<DepositeLog> depositLogs = new ArrayList<DepositeLog>();
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="account")
    private Collection<TrainingLog> trainingLogs = new ArrayList<TrainingLog>();
    
    public void AddDepositeLog(DepositeLog deposite) {
        //每次存钱后更新balance
        depositLogs.add(deposite);
        deposite.setAccount(this);
        balance = balance.add(deposite.getAmount());
    }
    
    public void AddTrainingLog(TrainingLog trainingLog) {
        //每次添加一条训练记录要更新balance
        trainingLogs.add(trainingLog);
        trainingLog.setAccount(this);
        trainingLog.setTrainee(this.owner);
        balance = balance.subtract(unitPrice);
    }
}
