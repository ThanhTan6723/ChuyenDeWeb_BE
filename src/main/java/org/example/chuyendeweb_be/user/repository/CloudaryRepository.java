package org.example.chuyendeweb_be.user.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudaryRepository {
    String upLoadImage(MultipartFile file) throws IOException;
}
