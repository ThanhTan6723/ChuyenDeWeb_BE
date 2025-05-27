package org.example.chuyendeweb_be.user.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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