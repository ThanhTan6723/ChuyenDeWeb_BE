package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Reviewimage}
 */
@Value
public class ReviewimageDTO implements Serializable {
    Long id;
    String imgAssetId;
}