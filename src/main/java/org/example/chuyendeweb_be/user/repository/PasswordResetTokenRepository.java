package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<ResetPassword, Long> {
    Optional<ResetPassword> findByToken(String token);
}
