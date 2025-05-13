package com.insurance.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "insurance_quotes")
public class InsuranceQuote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int age;
    private String insuranceType;
    private double coverageAmount;
    private double premium;
    private LocalDateTime calculationDate;
    
    @PrePersist
    protected void onCreate() {
        calculationDate = LocalDateTime.now();
    }
} 