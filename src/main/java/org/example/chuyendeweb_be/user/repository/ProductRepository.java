package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}

