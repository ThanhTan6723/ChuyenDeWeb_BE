package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

}
