package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Discounttype}
 */
@Value
public class DiscounttypeDTO implements Serializable {
    Long id;
    String type;
}