package org.example.chuyendeweb_be.dto;

import lombok.*;
import org.example.chuyendeweb_be.entity.Category;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String brandName;
    private String categoryName;
    private String mainImage;
    private List<ProductVariantDTO> variants;
}