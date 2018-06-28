package com.smarttraining.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="t_training_log")
@SQLDelete(sql="UPDATE t_training_log SET active=false WHERE id=?")
@Where(clause="active = true")
public class TrainingLog extends BaseEntity {
    
    @OneToOne
    @JoinColumn(name="trainer_id")
    private User trainer;
    
    @OneToOne
    @JoinColumn(name="trainee_id")
    private User trainee;
    
    @OneToOne
    @JoinColumn(name="account_id")
    private TrainingAccount account;
    
    @Column(nullable=true)
    private String comment;
}
