package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {


    Optional<Brand> findByName(String brandName);
}
