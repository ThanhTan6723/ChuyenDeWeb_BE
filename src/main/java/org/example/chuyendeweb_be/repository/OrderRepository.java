package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
