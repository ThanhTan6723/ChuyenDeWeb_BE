package org.example.chuyendeweb_be.dto;

import lombok.*;
import org.example.chuyendeweb_be.entity.Category;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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