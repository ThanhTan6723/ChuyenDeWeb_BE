//package org.example.chuyendeweb_be.controller;
//
//import org.example.chuyendeweb_be.service.ProductService;
//import org.example.chuyendeweb_be.service.ProductService.ProductDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/products")
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//
//    @GetMapping
//    public List<ProductDTO> getAllProducts() {
//        return productService.getAllProducts();
//    }
//
//    @PostMapping
//    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
//        return productService.addProduct(productDTO);
//    }
//}