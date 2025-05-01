package org.example.chuyendeweb_be.dto;

import lombok.Value;
import org.example.chuyendeweb_be.entity.Category;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Product}
 */
@Value
public class ProductDTO implements Serializable {
    Long id;
    String name;
    Double price;
    Double weight;
    String image;
    String description;
    CategoryDTO category;
    Integer viewCount;
}