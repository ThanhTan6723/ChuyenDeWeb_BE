package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}