package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {


}
