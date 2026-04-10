package com.tendanz.pricing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a quote for an insurance product.
 * Contains client information, pricing details, and applied rules.
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

    @NotNull(message = "Product is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Zone is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @NotBlank(message = "Client name cannot be blank")
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @NotNull(message = "Client age is mandatory")
    @Min(value = 18, message = "Client age must be at least 18")
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    @Column(name = "client_age", nullable = false)
    private Integer clientAge;

    @NotNull(message = "Base price is mandatory")
    @PositiveOrZero(message = "Base price cannot be negative")
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @NotNull(message = "Final price is mandatory")
    @PositiveOrZero(message = "Final price cannot be negative")
    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @NotBlank(message = "Applied rules cannot be blank")
    @Column(name = "applied_rules", nullable = false, columnDefinition = "CLOB")
    private String appliedRules;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
