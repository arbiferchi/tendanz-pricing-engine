package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Quote entity.
 * Provides database operations for quotes.
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /**
     * Retrieves all quotes associated with a specific client name.
     *
     * @param clientName The name of the client to search for.
     * @return A list of quotes matching the given client name.
     */
    List<Quote> findByClientName(String clientName);
    
    /**
     * Retrieves all quotes associated with a specific product ID with pagination.
     *
     * @param productId The ID of the product.
     * @param pageable Pagination configuration.
     * @return A page of quotes for the specified product.
     */
    Page<Quote> findByProductId(Long productId, Pageable pageable);

    /**
     * Retrieves all quotes where the final price is greater than or equal to the predefined threshold with pagination.
     *
     * @param minPrice The minimum final price (inclusive).
     * @param pageable Pagination configuration.
     * @return A page of quotes matching the condition, ordered by price descending.
     */
    @Query("SELECT q FROM Quote q WHERE q.finalPrice >= :minPrice ORDER BY q.finalPrice DESC")
    Page<Quote> findQuotesAboveThreshold(@Param("minPrice") BigDecimal minPrice, Pageable pageable);

    /**
     * Retrieves all quotes where the final price is greater than or equal to the predefined threshold with pagination.
     */
    Page<Quote> findByFinalPriceGreaterThanEqual(BigDecimal minPrice, Pageable pageable);

    /**
     * Retrieves all quotes for a specific product and with a final price greater than or equal to the threshold with pagination.
     */
    Page<Quote> findByProductIdAndFinalPriceGreaterThanEqual(Long productId, BigDecimal minPrice, Pageable pageable);

}
