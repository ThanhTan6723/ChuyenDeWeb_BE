package org.example.chuyendeweb_be.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        // Tạo một public_id ngẫu nhiên cho file
        String publicId = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<>();
        params.put("public_id", publicId);
        params.put("folder", "products"); // Folder trên Cloudinary để lưu ảnh
        params.put("overwrite", true);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("url");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, null);
    }
}