package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}