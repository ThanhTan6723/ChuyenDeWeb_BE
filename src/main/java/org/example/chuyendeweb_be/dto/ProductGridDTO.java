package org.example.chuyendeweb_be.dto;

import lombok.Data;

@Data
public class ProductGridDTO {
    private Long id;
    private String name;
    private Double price;
    private String attributes;
    private String description;
    private String mainImageUrl;
    private String brand;
    private String category;
    private Integer stock; // Tổng số lượng tồn kho từ các variant
}