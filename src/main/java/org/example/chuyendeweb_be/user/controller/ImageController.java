package org.example.chuyendeweb_be.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.ImageUploadResponse;
import org.example.chuyendeweb_be.user.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadResult = cloudinaryService.upLoadImage(file);
            JSONObject jsonResult = new JSONObject(uploadResult);
            return ResponseEntity.ok(new ImageUploadResponse(
                    jsonResult.getString("imageUrl"),
                    jsonResult.getString("publicId"),
                    "Image uploaded successfully"
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ImageUploadResponse(
                    null,
                    null,
                    "Failed to upload image: " + e.getMessage()
            ));
        }
    }
}