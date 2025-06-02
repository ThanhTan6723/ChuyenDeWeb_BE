package org.example.chuyendeweb_be.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.ProductGridDTO;
import org.example.chuyendeweb_be.user.dto.ProductDetailDTO;
import org.example.chuyendeweb_be.user.entity.Product;
import org.example.chuyendeweb_be.user.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/grid")
    public ResponseEntity<Map<String, Object>> getProductsForGrid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
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
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<Map<String, Object>> getBestSellers(
            @RequestParam(defaultValue = "6") int size) {
        if (size <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid size parameter"));
        }
        List<Product> bestSellers = productService.getBestSellers(size);
        List<ProductGridDTO> products = productService.mapToDTO(bestSellers);
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductDetails(@PathVariable Long id) {
        try {
            ProductDetailDTO product = productService.getProductDetails(id);
            if (product == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Product not found with ID: " + id));
            }
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}