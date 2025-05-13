package com.insurance.controller;

import com.insurance.model.InsuranceQuote;
import com.insurance.repository.InsuranceQuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InsuranceController {

    @Autowired
    private InsuranceQuoteRepository quoteRepository;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("quote", new InsuranceQuote());
        model.addAttribute("quotes", quoteRepository.findAll());
        
        // Add risk assessment data
        model.addAttribute("ageRiskData", getAgeRiskData());
        model.addAttribute("typeRiskData", getTypeRiskData());
        
        return "index";
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        model.addAttribute("quotes", quoteRepository.findAll());
        return "history";
    }

    @GetMapping("/risk-analytics")
    public String showRiskAnalytics(Model model) {
        model.addAttribute("ageRiskData", getAgeRiskData());
        model.addAttribute("typeRiskData", getTypeRiskData());
        return "risk-analytics";
    }

    @PostMapping("/calculate")
    public String calculateQuote(@ModelAttribute InsuranceQuote quote) {
        // Calculate base premium based on coverage amount
        double basePremium = quote.getCoverageAmount() * 0.05;
        
        // Calculate age factor
        double ageFactor = getAgeFactor(quote.getAge());
        
        // Calculate coverage risk factor
        double coverageRiskFactor = getCoverageRiskFactor(quote.getCoverageAmount());
        
        // Calculate insurance type factor
        double typeFactor = getInsuranceTypeFactor(quote.getInsuranceType());
        
        // Calculate final premium with all factors
        double finalPremium = basePremium * ageFactor * coverageRiskFactor * typeFactor;
        
        quote.setPremium(finalPremium);
        quoteRepository.save(quote);
        
        return "redirect:/";
    }

    @DeleteMapping("/delete-quote/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteQuote(@PathVariable Long id) {
        try {
            quoteRepository.deleteById(id);
            return ResponseEntity.ok("Quote deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete quote: " + e.getMessage());
        }
    }

    private double getAgeFactor(int age) {
        if (age < 25) return 1.5;
        if (age < 35) return 1.2;
        if (age < 45) return 1.0;
        if (age < 55) return 1.1;
        if (age < 65) return 1.3;
        return 2.0;
    }

    private double getCoverageRiskFactor(double coverageAmount) {
        if (coverageAmount <= 500000) return 1.0;
        if (coverageAmount <= 1000000) return 1.2;
        if (coverageAmount <= 2000000) return 1.4;
        if (coverageAmount <= 5000000) return 1.6;
        return 2.0;
    }

    private double getInsuranceTypeFactor(String insuranceType) {
        switch (insuranceType) {
            case "LIFE": return 1.2;
            case "HEALTH": return 1.5;
            case "AUTO": return 1.8;
            default: return 1.0;
        }
    }

    // Risk assessment data for age groups
    private Map<String, Object> getAgeRiskData() {
        Map<String, Object> data = new HashMap<>();
        
        // Age ranges
        String[] ageRanges = {"18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        
        // Risk factors for each insurance type by age
        double[] lifeRiskFactors = {1.8, 1.5, 1.2, 1.3, 1.6, 2.2};
        double[] healthRiskFactors = {1.2, 1.1, 1.0, 1.2, 1.5, 2.0};
        double[] autoRiskFactors = {2.0, 1.8, 1.5, 1.3, 1.4, 1.6};
        
        data.put("labels", ageRanges);
        data.put("lifeRiskFactors", lifeRiskFactors);
        data.put("healthRiskFactors", healthRiskFactors);
        data.put("autoRiskFactors", autoRiskFactors);
        
        return data;
    }

    // Risk assessment data for insurance types
    private Map<String, Object> getTypeRiskData() {
        Map<String, Object> data = new HashMap<>();
        
        // Insurance types and their descriptions
        String[] types = {"LIFE", "HEALTH", "AUTO"};
        String[] descriptions = {
            "Life insurance risk based on age and health factors",
            "Health insurance risk based on medical history and age",
            "Auto insurance risk based on driving history and vehicle type"
        };
        
        data.put("labels", types);
        data.put("descriptions", descriptions);
        
        return data;
    }
} 