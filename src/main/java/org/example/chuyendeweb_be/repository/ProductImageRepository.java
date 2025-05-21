package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}