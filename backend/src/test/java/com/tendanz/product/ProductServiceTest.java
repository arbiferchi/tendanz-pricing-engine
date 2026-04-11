package com.tendanz.product;

import com.tendanz.pricing.dto.ProductCreationRequest;
import com.tendanz.pricing.dto.ProductResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Validates the creation of a 4th product alongside its specific pricing rules.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @InjectMocks
    private ProductService productService;

    private ProductCreationRequest request;

    @BeforeEach
    void setUp() {
        request = ProductCreationRequest.builder()
                .name("Assurance Voyage")
                .description("Couverture pour les voyages à l'étranger")
                .baseRate(new BigDecimal("150.00"))
                .ageFactorYoung(new BigDecimal("1.10"))
                .ageFactorAdult(new BigDecimal("1.00"))
                .ageFactorSenior(new BigDecimal("1.30"))
                .ageFactorElderly(new BigDecimal("1.60"))
                .build();
    }

    @Test
    @DisplayName("Should successfully create a 4th product with specific rules")
    void testCreateProductWithRules_Success() {
        // Arrange
        Product savedProduct = Product.builder()
                .id(4L)
                .name(request.getName())
                .description(request.getDescription())
                .build();
                
        PricingRule savedRule = PricingRule.builder()
                .id(4L)
                .product(savedProduct)
                .baseRate(request.getBaseRate())
                .ageFactorYoung(request.getAgeFactorYoung())
                .ageFactorAdult(request.getAgeFactorAdult())
                .ageFactorSenior(request.getAgeFactorSenior())
                .ageFactorElderly(request.getAgeFactorElderly())
                .build();

        // Assume product doesn't exist
        when(productRepository.existsByName(request.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(pricingRuleRepository.save(any(PricingRule.class))).thenReturn(savedRule);

        // Act
        ProductResponse response = productService.createProductWithRules(request);

        // Assert
        assertNotNull(response);
        assertEquals(4L, response.getId());
        assertEquals("Assurance Voyage", response.getName());
        assertEquals(new BigDecimal("150.00"), response.getBaseRate());
        
        verify(productRepository, times(1)).save(any(Product.class));
        verify(pricingRuleRepository, times(1)).save(any(PricingRule.class));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException if product name already exists")
    void testCreateProduct_NameExists_ThrowsException() {
        // Arrange
        when(productRepository.existsByName(request.getName())).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.createProductWithRules(request));
            
        assertEquals("A product with the name 'Assurance Voyage' already exists", exception.getMessage());
        
        // Verify we never attempted to save
        verify(productRepository, never()).save(any(Product.class));
        verify(pricingRuleRepository, never()).save(any(PricingRule.class));
    }
}