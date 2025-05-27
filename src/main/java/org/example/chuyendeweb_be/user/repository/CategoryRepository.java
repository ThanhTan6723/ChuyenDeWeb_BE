package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
