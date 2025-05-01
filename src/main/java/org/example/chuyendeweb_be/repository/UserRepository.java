package org.example.chuyendeweb_be.repository;

import org.example.chuyendeweb_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
