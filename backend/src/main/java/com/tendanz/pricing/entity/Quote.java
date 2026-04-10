package com.tendanz.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a quote for an insurance product.
 * Contains client information, pricing details, and applied rules.
 *
 */
@Entity
@Table(name = "quote", indexes = {
        @Index(name = "idx_quote_product_id", columnList = "product_id"),
        @Index(name = "idx_quote_zone_id", columnList = "zone_id"),
        @Index(name = "idx_quote_client_name", columnList = "client_name"),
        @Index(name = "idx_quote_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_age", nullable = false)
    private Integer clientAge;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "applied_rules", nullable = false, columnDefinition = "CLOB")
    private String appliedRules;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
