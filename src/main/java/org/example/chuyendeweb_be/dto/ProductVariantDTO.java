package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private String attribute;
    private String variant;
    private Double price;
    private Integer quantity;
    private List<String> images;
}
