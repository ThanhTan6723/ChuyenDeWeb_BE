package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByUsernameOrEmailOrPhone(String username, String email, String phone);
    Optional<User> findById(Long id);

    boolean existsUserByName(String name);
    boolean existsUserByEmail(String email);
    boolean existsUserByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
