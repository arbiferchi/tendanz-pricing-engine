package com.tendanz.pricing.controller;

import com.tendanz.pricing.dto.ProductCreationRequest;
import com.tendanz.pricing.dto.ProductResponse;
import com.tendanz.pricing.entity.Product;
import com.tendanz.pricing.repository.ProductRepository;
import com.tendanz.pricing.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

/**
 * REST Controller for retrieving available insurance products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * Get all available products overview in a paginated format.
     *
     * @param pageable pagination information
     * @return paginated list of all products
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        log.info("Received request to fetch products list - page {} size {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.getAllProducts(pageable));      
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * @param id The ID to retrieve
     * @return The requested product mapped response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Received request to fetch product details for ID: {}", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Add a new product (e.g. creating a 4th product with specific naming and rules).
     *
     * @param request Validated product and pricing payload
     * @return the newly saved Product response DTO
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreationRequest request) {
        log.info("Received request to create a new product with name: {}", request.getName());
        ProductResponse newlyCreated = productService.createProductWithRules(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newlyCreated);    
    }

    /**
     * Updates an existing product to correct configurations.
     *
     * @param id      The target ID to modify.
     * @param request Validated payload modifications.
     * @return the updated version of the product overview.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreationRequest request) {
        log.info("Received request to update product configurations for ID: {}", id);
        return ResponseEntity.ok(productService.updateProduct(id, request));    
    }
}