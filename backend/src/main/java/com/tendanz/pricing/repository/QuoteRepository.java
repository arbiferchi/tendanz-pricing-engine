package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.Quote;
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
     * Retrieves all quotes associated with a specific product ID.
     *
     * @param productId The ID of the product.
     * @return A list of quotes for the specified product.
     */
    List<Quote> findByProductId(Long productId);

    /**
     * Retrieves all quotes where the final price is strictly greater than the predefined threshold.
     *
     * @param threshold The minimum final price (exclusive).
     * @return A list of quotes exceeding the specified threshold.
     */
    @Query("SELECT q FROM Quote q WHERE q.finalPrice > :threshold")
    List<Quote> findQuotesAboveThreshold(@Param("threshold") BigDecimal threshold);

    /**
     * Retrieves all quotes where the final price is greater than or equal to the predefined threshold.
     */
    List<Quote> findByFinalPriceGreaterThanEqual(BigDecimal minPrice);

    /**
     * Retrieves all quotes for a specific product and with a final price greater than or equal to the threshold.
     */
    List<Quote> findByProductIdAndFinalPriceGreaterThanEqual(Long productId, BigDecimal minPrice);

}
