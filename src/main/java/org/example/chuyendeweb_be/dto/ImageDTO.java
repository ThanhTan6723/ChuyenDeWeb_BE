package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Image}
 */
@Value
public class ImageDTO implements Serializable {
    Long id;
    String imgAssetId;
}