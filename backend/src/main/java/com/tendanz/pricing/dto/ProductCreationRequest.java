package com.tendanz.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new Product and its associated Pricing Rules together.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreationRequest {

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Base rate is required")
    @DecimalMin(value = "0.01", message = "Base rate must be greater than zero")
    private BigDecimal baseRate;

    @NotNull(message = "Age factor (young) is required")
    @DecimalMin(value = "0.01", message = "Age factor must be greater than zero")
    private BigDecimal ageFactorYoung;

    @NotNull(message = "Age factor (adult) is required")
    @DecimalMin(value = "0.01", message = "Age factor must be greater than zero")
    private BigDecimal ageFactorAdult;

    @NotNull(message = "Age factor (senior) is required")
    @DecimalMin(value = "0.01", message = "Age factor must be greater than zero")
    private BigDecimal ageFactorSenior;

    @NotNull(message = "Age factor (elderly) is required")
    @DecimalMin(value = "0.01", message = "Age factor must be greater than zero")
    private BigDecimal ageFactorElderly;
}
