package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Orderdetail}
 */
@Value
public class OrderdetailDTO implements Serializable {
    Long id;
    BigDecimal productPrice;
    Integer quantity;
    BigDecimal priceWithQuantity;
}