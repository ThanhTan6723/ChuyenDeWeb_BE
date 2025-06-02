package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findById(Long aLong);
}
