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

    @Query("SELECT p FROM Product p JOIN p.productVariantList pv LEFT JOIN pv.productImageList pi WHERE pi.mainImage = true OR pi IS NULL ORDER BY pv.price ASC")
    Page<Product> findAllByPriceAsc(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.productVariantList pv LEFT JOIN pv.productImageList pi WHERE pi.mainImage = true OR pi IS NULL ORDER BY pv.price DESC")
    Page<Product> findAllByPriceDesc(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.productVariantList pv LEFT JOIN pv.productImageList pi WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword))) AND (pi.mainImage = true OR pi IS NULL) ORDER BY pv.price ASC")
    Page<Product> findByNameOrBrandByPriceAsc(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.productVariantList pv LEFT JOIN pv.productImageList pi WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword))) AND (pi.mainImage = true OR pi IS NULL) ORDER BY pv.price DESC")
    Page<Product> findByNameOrBrandByPriceDesc(@Param("keyword") String keyword, Pageable pageable);
}