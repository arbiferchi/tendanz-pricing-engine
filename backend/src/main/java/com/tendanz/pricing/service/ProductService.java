package com.tendanz.pricing.service;

import com.tendanz.pricing.dto.ProductCreationRequest;
import com.tendanz.pricing.dto.ProductResponse;
import com.tendanz.pricing.entity.PricingRule;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.repository.PricingRuleRepository;
import com.tendanz.pricing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Service responsible for managing Products and their associated Pricing Rules.
 * Respects SOLID principles by segregating Product lifecycle operations from Pricing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final PricingRuleRepository pricingRuleRepository;

    /**
     * Gets all available products mapped to responses, with pagination.
     *
     * @param pageable pagination parameters.
     * @return Paginated products mapping to response.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination.");
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Retrieves a single product by its ID correctly mapped to a response.
     *
     * @param id The ID of the product.
     * @return The product response.
     * @throws com.tendanz.pricing.exception.ResourceNotFoundException if not found.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new com.tendanz.pricing.exception.ResourceNotFoundException("Product not found with id: " + id));
        
        return mapToResponse(product);
    }

    /**
     * Creates a new Product simultaneously with its unique PricingRule constraints.
     *
     * @param request Validated product and pricing payload
     * @return The fully persisted Product overview
     */
    @Transactional
    public ProductResponse createProductWithRules(ProductCreationRequest request) {
        log.info("Creating new product: {}", request.getName());

        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A product with the name '" + request.getName() + "' already exists");
        }

        // 1. Create and Save Product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        
        Product savedProduct = productRepository.save(product);

        // 2. Create and Save PricingRule tied to this product
        PricingRule pricingRule = PricingRule.builder()
                .product(savedProduct)
                .baseRate(request.getBaseRate())
                .ageFactorYoung(request.getAgeFactorYoung())
                .ageFactorAdult(request.getAgeFactorAdult())
                .ageFactorSenior(request.getAgeFactorSenior())
                .ageFactorElderly(request.getAgeFactorElderly())
                .createdAt(LocalDateTime.now())
                .build();

        PricingRule savedRule = pricingRuleRepository.save(pricingRule);
        
        log.info("Successfully created product '{}' with ID: {} and its active pricing rules.", savedProduct.getName(), savedProduct.getId());

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .baseRate(savedRule.getBaseRate())
                .ageFactorYoung(savedRule.getAgeFactorYoung())
                .ageFactorAdult(savedRule.getAgeFactorAdult())
                .ageFactorSenior(savedRule.getAgeFactorSenior())
                .ageFactorElderly(savedRule.getAgeFactorElderly())
                .build();
    }

    /**
     * Updates an existing product and its pricing rules.
     *
     * @param id      The ID of the product to update.
     * @param request The updated product payload.
     * @return The updated product response.
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductCreationRequest request) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new com.tendanz.pricing.exception.ResourceNotFoundException("Product not found with id: " + id));

        // Ensure name uniqueness when modifying name
        if (!existingProduct.getName().equalsIgnoreCase(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A product with the name '" + request.getName() + "' already exists");
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());

        // Update pricing rules
        PricingRule pricingRule = pricingRuleRepository.findByProductId(existingProduct.getId())
                .orElseThrow(() -> new com.tendanz.pricing.exception.ResourceNotFoundException("Pricing rules not found for product id: " + id));
        
        pricingRule.setBaseRate(request.getBaseRate());
        pricingRule.setAgeFactorYoung(request.getAgeFactorYoung());
        pricingRule.setAgeFactorAdult(request.getAgeFactorAdult());
        pricingRule.setAgeFactorSenior(request.getAgeFactorSenior());
        pricingRule.setAgeFactorElderly(request.getAgeFactorElderly());

        productRepository.save(existingProduct);
        pricingRuleRepository.save(pricingRule);

        log.info("Successfully updated product '{}' with ID: {}", existingProduct.getName(), existingProduct.getId());

        return mapToResponse(existingProduct);
    }
    
    /**
     * Mappings a Product entity to a ProductResponse DTO extracting rule details.
     * 
     * @param product The origin product.
     * @return the Response.
     */
    private ProductResponse mapToResponse(Product product) {
        PricingRule rule = pricingRuleRepository.findByProductId(product.getId())
                .orElseThrow(() -> new com.tendanz.pricing.exception.ResourceNotFoundException("Pricing rules not found for product id: " + product.getId()));
        
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .baseRate(rule.getBaseRate())
                .ageFactorYoung(rule.getAgeFactorYoung())
                .ageFactorAdult(rule.getAgeFactorAdult())
                .ageFactorSenior(rule.getAgeFactorSenior())
                .ageFactorElderly(rule.getAgeFactorElderly())
                .build();
    }
}
