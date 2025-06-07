package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.EVoucher;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EVoucherRepository extends JpaRepository<EVoucher, Long> {
    Optional<EVoucher> findByUserAndVoucher(User user, Voucher voucher);
    List<EVoucher> findByUser(User user);
}
