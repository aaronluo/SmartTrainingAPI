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
    
    @Column(precision=8, scale=2, nullable=false)
    private BigDecimal unitPrice;
    
    @Column(nullable=true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Column(nullable=true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Column(columnDefinition="int default 0")
    private int limitation;
    
    @OneToMany(mappedBy="training", cascade = CascadeType.ALL)
    @Where(clause="active=true")
    @OrderBy("update_date desc")
    private Collection<TrainingAccount> accounts = new ArrayList<TrainingAccount>();
}
