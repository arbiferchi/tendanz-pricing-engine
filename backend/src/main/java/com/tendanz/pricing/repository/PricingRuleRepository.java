package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for PricingRule entity.
 * Provides database operations for pricing rules.
 */
@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    /**
     * Find pricing rule by product ID.
     *
     * @param productId the product ID
     * @return an Optional containing the pricing rule if found
     */
    Optional<PricingRule> findByProductId(Long productId);
}
