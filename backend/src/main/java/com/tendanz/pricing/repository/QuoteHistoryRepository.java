package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.QuoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for QuoteHistory entity.
 * Provides database operations for tracking quote revisions.
 */
@Repository
public interface QuoteHistoryRepository extends JpaRepository<QuoteHistory, Long> {

    /**
     * Retrieves the modification history of a specific quote, ordered by the latest modification first.
     *
     * @param quoteId The ID of the quote.
     * @return A list of history records for the quote.
     */
    List<QuoteHistory> findByQuoteIdOrderByModifiedAtDesc(Long quoteId);
}
