package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Category}
 */
@Value
public class CategoryDTO implements Serializable {
    Long id;
    String name;
}