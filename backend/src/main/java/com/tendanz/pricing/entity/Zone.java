package com.tendanz.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entity representing a geographic zone with associated risk coefficient.
 * Used in pricing calculations to adjust rates based on location.
 */
@Entity
@Table(name = "zone", indexes = {
        @Index(name = "idx_zone_code", columnList = "code", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "risk_coefficient", nullable = false)
    private BigDecimal riskCoefficient;
}
