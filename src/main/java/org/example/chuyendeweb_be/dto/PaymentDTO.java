package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Payment}
 */
@Value
public class PaymentDTO implements Serializable {
    Long id;
    String mehthodName;
}