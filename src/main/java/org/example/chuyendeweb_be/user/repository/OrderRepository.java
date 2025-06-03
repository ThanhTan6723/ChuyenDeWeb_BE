package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Order;
import org.example.chuyendeweb_be.user.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findAll();
    Optional<Order> findByVnpTxnRef(Long vnpTxnRef);

}
