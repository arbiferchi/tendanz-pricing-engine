package com.tendanz.pricing.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for incoming quote creation requests.
 * Includes validation constraints for all fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequest {

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotBlank(message = "Zone code cannot be blank")
    private String zoneCode;

    @NotBlank(message = "Client name cannot be blank")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    private String clientName;

    @NotNull(message = "Client age cannot be null")
    @Min(value = 18, message = "Client must be at least 18 years old")
    @Max(value = 99, message = "Client age cannot exceed 99")
    private Integer clientAge;
}
