package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for Zone entity.
 * Provides database operations for geographic zones.
 */
@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    /**
     * Find a zone by its code.
     *
     * @param code the zone code
     * @return an Optional containing the zone if found
     */
    Optional<Zone> findByCode(String code);
}
