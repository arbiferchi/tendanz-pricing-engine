package com.tendanz.pricing.controller;

import com.tendanz.pricing.dto.QuoteRequest;
import com.tendanz.pricing.dto.QuoteResponse;
import com.tendanz.pricing.service.PricingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tendanz.pricing.exception.ResourceNotFoundException;

import java.util.List;

/**
 * REST Controller for managing quotes.
 * Handles all quote-related API endpoints.
 *
 * TODO: Implement the following endpoints:
 * - POST /api/quotes       -> Create a new quote (call PricingService.calculateQuote)
 * - GET  /api/quotes/{id}  -> Already implemented below as reference
 * - GET  /api/quotes       -> Get all quotes with optional filters (productId, minPrice)
 */
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@Slf4j
public class QuoteController {

    private final PricingService pricingService;

    /**
     * Create a new quote based on the provided request parameters.
     *
     * @param request Validated quote request payload
     * @return QuoteResponse wrapped in HTTP 201 Created entity
     */
    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody QuoteRequest request) {
        log.info("Received request to create a new quote for client: {}", request.getClientName());
        
        QuoteResponse response = pricingService.calculateQuote(request);
        log.info("Successfully created quote with ID: {}", response.getQuoteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a quote by its unique identifier.
     * 
     * This endpoint fetches an existing quote from the system using its ID.
     * The retrieved quote includes all pricing details, applied rules, and timestamps.
     *
     * @param id the unique identifier of the quote to retrieve (must be positive)
     * @return ResponseEntity containing the QuoteResponse with HTTP 200 OK status on success
     * 
     * @throws ResourceNotFoundException (caught by GlobalExceptionHandler)
     *         Returns HTTP 404 NOT_FOUND if the quote with the given ID does not exist
     *
     * @apiNote 
     * - ID must be a positive integer greater than 0
     * - The quote must exist in the database; non-existent IDs result in 404 errors
     * - All nested entities (Product, Zone, Rules) are included in the response
     * 
     * @example
     * GET /api/quotes/1 -> Returns quote with ID 1
     * GET /api/quotes/999 -> Returns 404 if quote 999 doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> getQuote(
            @PathVariable 
            @Positive(message = "Quote ID must be a positive integer") 
            Long id) {
        log.info("Received request to fetch quote with ID: {}", id);
        
        try {
            QuoteResponse response = pricingService.getQuote(id);
            log.info("Successfully retrieved quote with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.warn("Quote not found for ID: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Get all quotes with optional filters.
     *
     * This endpoint retrieves quotes from the system optionally filtering by product ID 
     * and/or a minimum final price.
     *
     * @param productId optional product ID filter
     * @param minPrice optional minimum price filter
     * @return list of quotes matching the criteria wrapped in an HTTP 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getAllQuotes(
            @RequestParam(required = false) 
            @Positive(message = "Product ID must be a positive integer") Long productId,
            @RequestParam(required = false) 
            @Positive(message = "Minimum price must be a positive value") Double minPrice) {
        
        log.info("Received request to fetch quotes with filters - ProductId: {}, MinPrice: {}", productId, minPrice);
        
        List<QuoteResponse> responses = pricingService.getAllQuotes(productId, minPrice);
        
        log.info("Successfully retrieved {} quotes matching the criteria", responses.size());
        return ResponseEntity.ok(responses);
    }
}
