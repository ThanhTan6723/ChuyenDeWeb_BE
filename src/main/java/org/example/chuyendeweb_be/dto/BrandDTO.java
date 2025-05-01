package org.example.chuyendeweb_be.dto;

import lombok.Value;
import org.example.chuyendeweb_be.entity.Brand;

import java.io.Serializable;

/**
 * DTO for {@link Brand}
 */
@Value
public class BrandDTO implements Serializable {
    Long id;
    String name;
    String address;
}