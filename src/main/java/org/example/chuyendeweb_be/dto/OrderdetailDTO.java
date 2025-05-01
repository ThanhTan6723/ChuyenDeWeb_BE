package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderdetailDTO implements Serializable {
    Long id;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal priceWithQuantity;
}