package org.example.chuyendeweb_be.user.repository;

import org.example.chuyendeweb_be.user.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
