package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Productimage}
 */
@Value
public class ProductImageDTO implements Serializable {
    Long id;
    ImageDTO image;
}