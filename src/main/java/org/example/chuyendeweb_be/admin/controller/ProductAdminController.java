package org.example.chuyendeweb_be.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.ProductGridDTO;
import org.example.chuyendeweb_be.user.entity.Product;
import org.example.chuyendeweb_be.user.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductAdminController.class);

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            logger.info("Fetching products for admin, page: {}, size: {}", page, size);
            if (page < 0 || size <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid page or size parameters"));
            }
            Page<Product> productPage = productService.getAllProductsForGrid(page, size);
            List<ProductGridDTO> products = productService.mapToDTO(productPage);
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("totalPages", productPage.getTotalPages());
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}