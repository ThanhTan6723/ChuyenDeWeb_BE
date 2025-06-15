package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<Object> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE (:email IS NOT NULL AND u.email = :email) OR (:phone IS NOT NULL AND u.phone = :phone)")
    Optional<User> findByEmailOrPhone(String email, String phone);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.username = :username OR u.email = :username")
    Optional<User> findByUsernameOrEmailWithRole(String username);
}