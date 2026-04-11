package com.tendanz.pricing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing the modification history of a Quote.
 * Keeps track of changes made to quotes over time.
 */
@Entity
@Table(name = "quote_history", indexes = {
        @Index(name = "idx_quote_history_quote_id", columnList = "quote_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quote reference is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "previous_price")
    private BigDecimal previousPrice;

    @Column(name = "new_price")
    private BigDecimal newPrice;

    @NotBlank(message = "Change summary is mandatory")
    @Column(name = "change_summary", nullable = false, columnDefinition = "TEXT")
    private String changeSummary;

    @Column(name = "modified_at", nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        modifiedAt = LocalDateTime.now();
    }
}
