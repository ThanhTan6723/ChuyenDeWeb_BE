package org.example.chuyendeweb_be.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class OrderResponseDTO {
    private Long id;
    private Instant bookingDate;
    private Instant deliveryDate;
    private String consigneeName;
    private String consigneePhone;
    private String address;
    private String orderNotes;
    private BigDecimal ship;
    private BigDecimal discountValue;
    private BigDecimal totalMoney;
    private String orderStatus;
}