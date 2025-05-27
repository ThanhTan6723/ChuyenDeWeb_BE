package org.example.chuyendeweb_be.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String category;
    private List<ProductVariantDTO> variants;

}