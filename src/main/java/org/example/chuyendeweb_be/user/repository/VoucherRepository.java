package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsByCode(String code);
}