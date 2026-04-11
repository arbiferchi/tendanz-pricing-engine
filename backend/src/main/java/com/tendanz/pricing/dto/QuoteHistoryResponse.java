package com.tendanz.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for quote history records.
 * Provides a read-only timeline of modifications for a quote.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteHistoryResponse {

    private Long id;

    private Long quoteId;
    
    private BigDecimal previousPrice;
    
    private BigDecimal newPrice;
    
    private String changeSummary;

    private LocalDateTime modifiedAt;
}
