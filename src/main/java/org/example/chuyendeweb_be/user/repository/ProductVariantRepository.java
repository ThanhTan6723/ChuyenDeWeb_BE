package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}