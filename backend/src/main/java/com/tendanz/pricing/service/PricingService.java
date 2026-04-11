package com.tendanz.pricing.service;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.dto.QuoteHistoryResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.QuoteHistory;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.enums.AgeCategory;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteHistoryRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import com.tendanz.pricing.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;


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
    private final QuoteHistoryRepository quoteHistoryRepository;
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
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", request.getProductId());
                    return new ResourceNotFoundException("Product", "id", request.getProductId());
                });
        
        // 2. Validate and load the Zone
        Zone zone = zoneRepository.findByCode(request.getZoneCode())
                .orElseThrow(() -> {
                    log.error("Zone not found with code: {}", request.getZoneCode());
                    return new ResourceNotFoundException("Zone", "code", request.getZoneCode());
                });
        
        // 3. Load the PricingRule for the product
        PricingRule pricingRule = pricingRuleRepository.findByProductId(product.getId())
                .orElseThrow(() -> {
                    log.error("Pricing rule not found for product ID: {}", product.getId());
                    return new ResourceNotFoundException("PricingRule", "productId", product.getId());
                });
        
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
     * Helper to deserialize rules safely.
     * Prevents JsonProcessingException crashes on mangled JSON array strings.
     *
     * @param rulesJson the JSON encoded applied rules
     * @return the deserialized string list
     */
    private List<String> deserializeRules(String rulesJson) {
        if (rulesJson == null || rulesJson.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(rulesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON string: {}. Returning empty list.", rulesJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * Map a saved Quote entity to a QuoteResponse DTO.
     * Prevents null pointer exceptions by verifying nested relationships.
     *
     * @param quote the persisted quote entity
     * @param appliedRules the list of human-readable applied rules
     * @return the fully populated quote response DTO
     * @throws IllegalArgumentException if the provided quote is null
     */
    private QuoteResponse mapToResponse(Quote quote, List<String> appliedRules) {
        if (quote == null) {
            throw new IllegalArgumentException("Quote entity cannot be null when mapping to response");
        }

        String productName = quote.getProduct() != null ? quote.getProduct().getName() : "Unknown Product";
        String zoneName = quote.getZone() != null ? quote.getZone().getName() : "Unknown Zone";

        return QuoteResponse.builder()
                .quoteId(quote.getId())
                .productName(productName)
                .zoneName(zoneName)
                .clientName(quote.getClientName())
                .clientAge(quote.getClientAge())
                .basePrice(quote.getBasePrice())
                .finalPrice(quote.getFinalPrice())
                .appliedRules(appliedRules != null ? appliedRules : new ArrayList<>())
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
                .orElseThrow(() -> {
                    log.error("Quote not found with ID: {}", id);
                    return new ResourceNotFoundException("Quote", "id", id);
                });

        List<String> appliedRules = deserializeRules(quote.getAppliedRules());
        return mapToResponse(quote, appliedRules);
    }

    /**
     * Retrieves all quotes based on optional filtering criteria with pagination.
     * Supports filtering by product ID and/or a minimum final price.
     *
     * @param productId an optional product ID to filter the quotes.
     * @param minPrice an optional minimum final price to filter the quotes.
     * @param pageable pagination configuration.
     * @return a page of quote responses matching the given criteria.
     */
    public Page<QuoteResponse> getAllQuotes(Long productId, Double minPrice, Pageable pageable) {
        
        log.info("Fetching quotes with filters - productId: {}, minPrice: {}, page: {}", productId, minPrice, pageable.getPageNumber());
        
        Page<Quote> quotes;

        if (productId != null && minPrice != null) {
            quotes = quoteRepository.findByProductIdAndFinalPriceGreaterThanEqual(productId, BigDecimal.valueOf(minPrice), pageable);
        } else if (productId != null) {
            quotes = quoteRepository.findByProductId(productId, pageable);
        } else if (minPrice != null) {
            quotes = quoteRepository.findByFinalPriceGreaterThanEqual(BigDecimal.valueOf(minPrice), pageable);
        } else {
            quotes = quoteRepository.findAll(pageable);
        }

        return quotes.map(quote -> {
            List<String> rules = deserializeRules(quote.getAppliedRules());
            return mapToResponse(quote, rules);
        });
    }

    /**
     * Updates an existing quote and records the historical change.
     * 
     * @param id The quote ID to update
     * @param request The new quote details
     * @return The updated quote response
     */
    @Transactional
    public QuoteResponse updateQuote(Long id, QuoteRequest request) {
        log.info("Updating Quote ID: {} for Client: {}", id, request.getClientName());
        
        Quote existingQuote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", "id", id));
                
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
                
        Zone zone = zoneRepository.findByCode(request.getZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "code", request.getZoneCode()));
                
        PricingRule rule = pricingRuleRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pricing rule", "productId", product.getId()));

        // Calculate differences for summary
        List<String> changes = new ArrayList<>();
        if (!existingQuote.getProduct().getId().equals(product.getId())) {
            changes.add("Product: " + existingQuote.getProduct().getName() + " -> " + product.getName());
        }
        if (!existingQuote.getZone().getCode().equals(zone.getCode())) {
            changes.add("Zone: " + existingQuote.getZone().getName() + " -> " + zone.getName());
        }
        if (!existingQuote.getClientAge().equals(request.getClientAge())) {
            changes.add("Client Age: " + existingQuote.getClientAge() + " -> " + request.getClientAge());
        }
        if (!existingQuote.getClientName().equals(request.getClientName())) {
            changes.add("Client Name: " + existingQuote.getClientName() + " -> " + request.getClientName());
        }

        // If no changes, avoid processing
        if (changes.isEmpty()) {
            return mapToResponse(existingQuote, deserializeRules(existingQuote.getAppliedRules()));
        }

        // Perform new pricing calculation
        AgeCategory category = AgeCategory.fromAge(request.getClientAge());
        BigDecimal ageFactor = getAgeFactor(rule, category);
        BigDecimal baseRate = rule.getBaseRate();
        BigDecimal zoneRiskCoefficient = zone.getRiskCoefficient();

        BigDecimal finalPrice = baseRate
                .multiply(ageFactor)
                .multiply(zoneRiskCoefficient)
                .setScale(2, RoundingMode.HALF_UP);

        String changeSummary = String.join(", ", changes);
        
        // Track the old price and change summary in history table
        QuoteHistory history = QuoteHistory.builder()
                .quote(existingQuote)
                .previousPrice(existingQuote.getFinalPrice())
                .newPrice(finalPrice)
                .changeSummary(changeSummary)
                .build();
        quoteHistoryRepository.save(history);

        // Create new applied rules
        List<String> rulesList = new ArrayList<>();
        rulesList.add(String.format(java.util.Locale.US, "Produit (%s): Base %.2f", product.getName(), baseRate));
        rulesList.add(String.format(java.util.Locale.US, "Age (%s): x%.2f", category.name(), ageFactor));
        rulesList.add(String.format(java.util.Locale.US, "Zone Risk (%s): x%.2f", zone.getName(), zoneRiskCoefficient));
        
        String jsonRules = convertRulesToJson(rulesList);

        // Update the existing entity
        existingQuote.setProduct(product);
        existingQuote.setZone(zone);
        existingQuote.setClientName(request.getClientName());
        existingQuote.setClientAge(request.getClientAge());
        existingQuote.setBasePrice(baseRate);
        existingQuote.setFinalPrice(finalPrice);
        existingQuote.setAppliedRules(jsonRules);

        Quote updatedQuote = quoteRepository.save(existingQuote);

        return mapToResponse(updatedQuote, rulesList);
    }
    
    /**
     * Retrieve modification history for a given quote ID.
     */
    public List<QuoteHistoryResponse> getQuoteHistory(Long quoteId) {
        quoteRepository.findById(quoteId) // Verify existence
                .orElseThrow(() -> new ResourceNotFoundException("Quote", "id", quoteId));
                
        List<QuoteHistory> historyList = quoteHistoryRepository.findByQuoteIdOrderByModifiedAtDesc(quoteId);
        
        return historyList.stream()
                .map(history -> QuoteHistoryResponse.builder()
                        .id(history.getId())
                        .quoteId(history.getQuote().getId())
                        .previousPrice(history.getPreviousPrice())
                        .newPrice(history.getNewPrice())
                        .changeSummary(history.getChangeSummary())
                        .modifiedAt(history.getModifiedAt())
                        .build())
                .toList();
    }
}
