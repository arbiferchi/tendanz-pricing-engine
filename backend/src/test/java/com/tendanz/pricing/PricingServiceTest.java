package com.tendanz.pricing;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.entity.Quote;
import com.tendanz.pricing.entity.Zone;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.repository.QuoteRepository;
import com.tendanz.pricing.repository.ZoneRepository;
import com.tendanz.pricing.service.PricingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.tendanz.pricing.exception.ResourceNotFoundException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PricingService.
 *
 * TODO: Implement at least 5 test cases covering:
 * - Quote calculation for different age categories (YOUNG, ADULT, SENIOR, ELDERLY)
 * - Different zone risk coefficients
 * - Edge cases (minimum age 18, maximum age 99, boundary between categories)
 * - Error handling (invalid product ID, invalid zone code)
 * - Quote retrieval by ID
 *
 * The @BeforeEach setUp() method below creates test data you can use.
 * Add your test methods below the existing structure.
 */
@DataJpaTest
@Import({PricingService.class})
class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    private Product product;
    private Zone zoneTunis;
    private Zone zoneStandard;
    private Zone zoneSfax;
    private PricingRule pricingRule;

    @BeforeEach
    void setUp() {
        quoteRepository.deleteAllInBatch();
        pricingRuleRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        zoneRepository.deleteAllInBatch();
        
        quoteRepository.flush();
        pricingRuleRepository.flush();
        productRepository.flush();
        zoneRepository.flush();

        // Shared test product
        product = Product.builder()
                .name("Test Auto Insurance - Test")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .build();
        product = productRepository.saveAndFlush(product);

        // Grand Tunis Zone (from original template - 1.20)
        zoneTunis = Zone.builder()
                .code("TUN_TEST")
                .name("Grand Tunis Test")
                .riskCoefficient(BigDecimal.valueOf(1.20))
                .build();
        zoneTunis = zoneRepository.saveAndFlush(zoneTunis);

        // Standard Zone (1.00 risk coefficient for base calculations)
        zoneStandard = Zone.builder()
                .code("STD_TEST")
                .name("Standard Zone Test")
                .riskCoefficient(BigDecimal.valueOf(1.00))
                .build();
        zoneStandard = zoneRepository.saveAndFlush(zoneStandard);

        // Sfax Zone (1.15 risk coefficient for specific test)
        zoneSfax = Zone.builder()
                .code("SFX_TEST")
                .name("Sfax Zone Test")
                .riskCoefficient(BigDecimal.valueOf(1.15))
                .build();
        zoneSfax = zoneRepository.saveAndFlush(zoneSfax);

        // Standard rules
        pricingRule = PricingRule.builder()
                .product(product)
                .baseRate(BigDecimal.valueOf(500.00))
                .ageFactorYoung(BigDecimal.valueOf(1.30))
                .ageFactorAdult(BigDecimal.valueOf(1.00))
                .ageFactorSenior(BigDecimal.valueOf(1.20))
                .ageFactorElderly(BigDecimal.valueOf(1.50))
                .createdAt(LocalDateTime.now())
                .build();
        pricingRule = pricingRuleRepository.saveAndFlush(pricingRule);
    }

    /**
     * Test quote calculation for an adult client with 1.0 zone coefficient.
     * Expected: 500.00 base * 1.0 (adult) * 1.0 (Standard Zone) = 500.00
     */
    @Test
    @DisplayName("Test quote calculation for an adult client (age 25-45). Expected: 600.00")
    void testCalculateQuoteForAdult() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zoneTunis.getCode())
                .clientName("Adult Client")
                .clientAge(30)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response, "Response should not be null");
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(response.getBasePrice()));
        assertEquals(0, BigDecimal.valueOf(600.00).compareTo(response.getFinalPrice()));
    }

    /**
     * Test quote calculation for a young client with 1.0 zone coefficient.
     * Expected: 500.00 base * 1.3 (young) * 1.0 (Standard Zone) = 650.00
     */
    @Test
    @DisplayName("Test quote calculation for a young client (age 18-24). Expected: 780.00")
    void testCalculateQuoteForYoungClient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zoneTunis.getCode())
                .clientName("Young Client")
                .clientAge(20)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(780.00).compareTo(response.getFinalPrice()));
    }

    /**
     * Test quote calculation for a senior client using original template zone (Tunis = 1.20).
     * Expected: 500.00 base * 1.2 (senior) * 1.2 (Tunis) = 720.00
     */
    @Test
    @DisplayName("Test quote calculation for a senior client (age 46-65). Expected 720.00")
    void testCalculateQuoteForSeniorClient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zoneTunis.getCode())
                .clientName("Senior Client")
                .clientAge(55)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(720.00).compareTo(response.getFinalPrice()));
    }

    /**
     * Test quote calculation utilizing a newly added zone coefficient (Sfax = 1.15).
     * Expected: 500.00 base * 1.0 (adult) * 1.15 (Sfax) = 575.00
     */
    @Test
    @DisplayName("Calculate quote using specific zone risk coefficient (Sfax)")
    void testCalculateQuoteWithZoneRiskCoefficient() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode(zoneSfax.getCode())
                .clientName("Sfax Client")
                .clientAge(30)
                .build();

        QuoteResponse response = pricingService.calculateQuote(request);

        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(575.00).compareTo(response.getFinalPrice()));
    }

    /**
     * Test that requesting a quote with an invalid product ID
     * throws our structured ResourceNotFoundException cleanly.
     */
    @Test
    @DisplayName("Throw ResourceNotFoundException on non-existent product ID")
    void testCalculateQuoteWithInvalidProductId() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(99999L)
                .zoneCode(zoneStandard.getCode())
                .clientName("Invalid Product Client")
                .clientAge(30)
                .build();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> pricingService.calculateQuote(request));
        
        assertTrue(exception.getMessage().contains("Product"));
    }

    /**
     * Test that requesting a quote with an invalid zone code
     * throws our structured ResourceNotFoundException cleanly.
     */
    @Test
    @DisplayName("Throw ResourceNotFoundException on non-existent zone code")
    void testCalculateQuoteWithInvalidZoneCode() {
        QuoteRequest request = QuoteRequest.builder()
                .productId(product.getId())
                .zoneCode("INVALID_ZONE")
                .clientName("Invalid Zone Client")
                .clientAge(30)
                .build();

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> pricingService.calculateQuote(request));
        
        assertTrue(exception.getMessage().contains("Zone"));
    }

    /**
     * Test edge cases: exact boundary ages.
     * Ensures boundary logic (e.g., 24 vs 25) functions properly.
     */
    /**
     * (Bonus) Test edge cases: age boundaries.
     * - Age 24 should be YOUNG, age 25 should be ADULT
     * - Age 45 should be ADULT, age 46 should be SENIOR
     * - Age 65 should be SENIOR, age 66 should be ELDERLY
     */
    @Test
    @DisplayName("Validate calculations against specific age boundaries")
    void testEdgeCasesAgeBoundaries() {
        // 24 (YOUNG: 1.30 * 1.0 (Standard) * 500)
        QuoteRequest req24 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 24", 24);
        assertEquals(0, BigDecimal.valueOf(650.00).compareTo(pricingService.calculateQuote(req24).getFinalPrice()));

        // 25 (ADULT: 1.00 * 1.0 * 500)
        QuoteRequest req25 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 25", 25);
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(pricingService.calculateQuote(req25).getFinalPrice()));

        // 45 (ADULT: 1.00 * 1.0 * 500)
        QuoteRequest req45 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 45", 45);
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(pricingService.calculateQuote(req45).getFinalPrice()));

        // 46 (SENIOR: 1.20 * 1.0 * 500)
        QuoteRequest req46 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 46", 46);
        assertEquals(0, BigDecimal.valueOf(600.00).compareTo(pricingService.calculateQuote(req46).getFinalPrice()));
        
        // 65 (SENIOR: 1.20 * 1.0 * 500)
        QuoteRequest req65 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 65", 65);
        assertEquals(0, BigDecimal.valueOf(600.00).compareTo(pricingService.calculateQuote(req65).getFinalPrice()));
        
        // 66 (ELDERLY: 1.50 * 1.0 * 500)
        QuoteRequest req66 = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Age 66", 66);
        assertEquals(0, BigDecimal.valueOf(750.00).compareTo(pricingService.calculateQuote(req66).getFinalPrice()));
    }

    /**
     * Test quote retrieval by ID logic.
     */
    @Test
    @DisplayName("Retrieve a specific quote by ID from the database")
    void testQuoteRetrieval() {
        QuoteRequest request = new QuoteRequest(product.getId(), zoneStandard.getCode(), "Retrieval Client", 30);
        QuoteResponse createdResponse = pricingService.calculateQuote(request);

        QuoteResponse retrievedResponse = pricingService.getQuote(createdResponse.getQuoteId());
        
        assertNotNull(retrievedResponse);
        assertEquals(createdResponse.getQuoteId(), retrievedResponse.getQuoteId());
        assertEquals("Retrieval Client", retrievedResponse.getClientName());
        assertEquals(0, retrievedResponse.getFinalPrice().compareTo(createdResponse.getFinalPrice()));
    }

    /**
     * Test applied rules are properly converted back and forth from JSON CLOB to List.
     */
    @Test
    @DisplayName("Ensure applied rules are properly stored as JSON and parsed back into Lists")
    void testAppliedRulesStorage() {
        QuoteRequest request = new QuoteRequest(product.getId(), zoneSfax.getCode(), "Rules Tester", 30);
        QuoteResponse createdQuote = pricingService.calculateQuote(request);
        
        Quote entity = quoteRepository.findById(createdQuote.getQuoteId()).orElseThrow();
        
        // Assert native JSON string in the DB contains explicit descriptions
        String clobJson = entity.getAppliedRules();
        assertNotNull(clobJson);
        assertTrue(clobJson.contains("Base Rate"));
        
        // Assert deserialized DTO transforms it successfully back into list
        QuoteResponse retrievedQuote = pricingService.getQuote(createdQuote.getQuoteId());
        List<String> userRules = retrievedQuote.getAppliedRules();
        
        assertEquals(3, userRules.size());
        assertTrue(userRules.stream().anyMatch(r -> r.contains("500")), "Missing base rate rule");
        assertTrue(userRules.stream().anyMatch(r -> r.toUpperCase().contains("ADULT")), "Missing age category rule");
        assertTrue(userRules.stream().anyMatch(r -> r.contains("1.15") || r.contains("1,15")), "Missing zone coefficient rule");
    }
}
