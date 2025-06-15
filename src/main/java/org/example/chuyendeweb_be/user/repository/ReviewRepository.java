package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndIsAcceptTrue(Long productId);
}