package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Order}
 */
@Value
public class OrderDTO implements Serializable {
    Long id;
    Instant bookingDate;
    Instant deliveryDate;
    String consigneeName;
    String consigneePhone;
    BigDecimal ship;
    BigDecimal discountValue;
    BigDecimal totalMoney;
    String address;
    String orderNotes;
    String orderStatus;
}