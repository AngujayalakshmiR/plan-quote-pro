package com.insurance.repository;

import com.insurance.model.InsuranceQuote;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface InsuranceQuoteRepository extends JpaRepository<InsuranceQuote, Long> {
} 