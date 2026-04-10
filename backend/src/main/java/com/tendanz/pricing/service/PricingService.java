package com.tendanz.pricing.service;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.enums.AgeCategory;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * Service for handling pricing and quote calculations.
 * Manages the business logic for pricing rules and quote generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final QuoteRepository quoteRepository;
    private final ObjectMapper objectMapper;

    /**
     * Calculate a quote based on the provided request.
     *
     * TODO: Implement the calculateQuote method with the following logic:
     * 1. Validate and load the Product from productRepository (throw IllegalArgumentException if not found)
     * 2. Validate and load the Zone from zoneRepository by code (throw IllegalArgumentException if not found)
     * 3. Load the PricingRule for the product from pricingRuleRepository
     * 4. Determine the age category using AgeCategory.fromAge(clientAge)
     * 5. Get the appropriate age factor using getAgeFactor() helper below
     * 6. Calculate: finalPrice = baseRate × ageFactor × zoneRiskCoefficient (rounded to 2 decimals)
     * 7. Build an appliedRules list describing each step of the calculation
     * 8. Create and save a Quote entity with all calculated values
     * 9. Return a QuoteResponse using the mapToResponse() helper below
     *
     * @param request the quote request containing productId, zoneCode, clientName, clientAge
     * @return the calculated quote response
     * @throws IllegalArgumentException if product, zone, or pricing rule not found
     */
    @Transactional
    public QuoteResponse calculateQuote(QuoteRequest request) {
        
        // 1. Validate and load the Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));
        
        // 2. Validate and load the Zone
        Zone zone = zoneRepository.findByCode(request.getZoneCode())
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with code: " + request.getZoneCode()));
        
        // 3. Load the PricingRule for the product
        PricingRule pricingRule = pricingRuleRepository.findByProductId(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pricing rule not found for product ID: " + product.getId()));
        
        // 4. Determine age category
        AgeCategory ageCategory = AgeCategory.fromAge(request.getClientAge());
        
        // 5. Get the appropriate age factor using getAgeFactor() helper
        BigDecimal ageFactor = getAgeFactor(pricingRule, ageCategory);
        
        // 6. Calculate base rate and final price
        BigDecimal baseRate = pricingRule.getBaseRate();
        BigDecimal zoneRiskCoefficient = zone.getRiskCoefficient();
        BigDecimal finalPrice = baseRate.multiply(ageFactor).multiply(zoneRiskCoefficient).setScale(2, RoundingMode.HALF_UP);
        
        // 7. Build appliedRules list and convert to JSON
        List<String> rulesList = new ArrayList<>();
        rulesList.add(String.format("Base Rate (%s): %.2f TND", product.getName(), baseRate));
        rulesList.add(String.format("Age Multiplier (%s): x%.2f", ageCategory.name(), ageFactor));
        rulesList.add(String.format("Zone Risk (%s): x%.2f", zone.getName(), zoneRiskCoefficient));
        
        String appliedRulesJson = convertRulesToJson(rulesList);
        
        // 8. Create Quote entity
        Quote quote = Quote.builder()
                .product(product)
                .zone(zone)
                .clientName(request.getClientName())
                .clientAge(request.getClientAge())
                .basePrice(baseRate)
                .finalPrice(finalPrice)
                .appliedRules(appliedRulesJson)
                .build();
                
        // Save quote
        Quote savedQuote = quoteRepository.save(quote);
        
        // 9. Return QuoteResponse using mapToResponse() helper
        return mapToResponse(savedQuote, rulesList);
    }

    /**
     * Get the age factor for a specific age category from a pricing rule.
     * Computes the multiplier based on the client's current age.
     *
     * @param pricingRule the pricing rule containing age factors
     * @param ageCategory the evaluated age category (YOUNG, ADULT, SENIOR, ELDERLY)
     * @return the appropriate age factor as a BigDecimal
     * @throws IllegalArgumentException if pricingRule or ageCategory is null
     */
    private BigDecimal getAgeFactor(PricingRule pricingRule, AgeCategory ageCategory) {
        if (pricingRule == null) {
            throw new IllegalArgumentException("PricingRule cannot be null to compute age factor");
        }
        if (ageCategory == null) {
            throw new IllegalArgumentException("AgeCategory cannot be null to compute age factor");
        }

        return switch (ageCategory) {
            case YOUNG -> pricingRule.getAgeFactorYoung();
            case ADULT -> pricingRule.getAgeFactorAdult();
            case SENIOR -> pricingRule.getAgeFactorSenior();
            case ELDERLY -> pricingRule.getAgeFactorElderly();
        };
    }

    /**
     * Serializes a list of applied pricing rules into a valid JSON string.
     * This is required because rules are saved as a CLOB/String payload in the Quote Entity.
     *
     * @param rules the list of rule descriptions to serialize
     * @return the serialized JSON string representation, or "[]" gracefully on error
     */
    private String convertRulesToJson(List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            log.warn("convertRulesToJson was called with a null or empty rules list. Returning empty JSON array.");
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(rules);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize pricing rules to JSON string. Fallback to empty array.", e);
            return "[]";
        }
    }

    /**
     * Convert a Quote entity to a QuoteResponse DTO.
     * This helper is provided — use it in your calculateQuote implementation.
     *
     * @param quote the quote entity
     * @param appliedRules the list of applied rules
     * @return the quote response DTO
     */
    private QuoteResponse mapToResponse(Quote quote, List<String> appliedRules) {
        return QuoteResponse.builder()
                .quoteId(quote.getId())
                .productName(quote.getProduct().getName())
                .zoneName(quote.getZone().getName())
                .clientName(quote.getClientName())
                .clientAge(quote.getClientAge())
                .basePrice(quote.getBasePrice())
                .finalPrice(quote.getFinalPrice())
                .appliedRules(appliedRules)
                .createdAt(quote.getCreatedAt())
                .build();
    }

    /**
     * Get a quote by ID.
     * This method is provided as a reference for how to retrieve and return quotes.
     *
     * @param id the quote ID
     * @return the quote response
     * @throws IllegalArgumentException if quote not found
     */
    public QuoteResponse getQuote(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found with ID: " + id));

        List<String> appliedRules = deserializeRules(quote.getAppliedRules());
        return mapToResponse(quote, appliedRules);
    }

    /**
     * Deserialize the rules JSON string back to a list.
     *
     * @param rulesJson the JSON string
     * @return the list of rules
     */
    private List<String> deserializeRules(String rulesJson) {
        try {
            return objectMapper.readValue(rulesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            log.error("Error deserializing rules from JSON", e);
            return new ArrayList<>();
        }
    }
}
