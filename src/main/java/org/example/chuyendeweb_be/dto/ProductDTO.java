package org.example.chuyendeweb_be.dto;

import lombok.*;
import org.example.chuyendeweb_be.entity.Category;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long brandId;
    private Long categoryId;
    private Integer viewCount;
}