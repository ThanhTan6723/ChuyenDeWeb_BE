package org.example.chuyendeweb_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.dto.ProductGridDTO;
import org.example.chuyendeweb_be.entity.Product;
import org.example.chuyendeweb_be.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/grid")
    public ResponseEntity<Map<String, Object>> getProductsForGrid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Product> productPage = productService.getAllProductsForGrid(page, size);
        List<ProductGridDTO> products = productService.mapToDTO(productPage);
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
}