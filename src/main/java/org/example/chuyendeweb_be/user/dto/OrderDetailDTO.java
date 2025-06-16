package org.example.chuyendeweb_be.user.dto;

import lombok.*;

@Data
public class OrderDetailDTO {
    private Long productVariantId;
    private Integer quantity;
}