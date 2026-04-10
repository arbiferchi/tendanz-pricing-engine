package com.tendanz.pricing.repository;

import com.tendanz.pricing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Product entity.
 * Provides database operations for products.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
