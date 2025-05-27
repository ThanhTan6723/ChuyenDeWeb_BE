package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
