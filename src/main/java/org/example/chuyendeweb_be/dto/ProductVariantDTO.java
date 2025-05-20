package org.example.chuyendeweb_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private ProductDTO product;
    private String productAttribute;
    private String variant;
    private double price;
    private int quantity;

}
