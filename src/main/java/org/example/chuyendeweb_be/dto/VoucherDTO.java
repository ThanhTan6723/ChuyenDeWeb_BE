package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO implements Serializable {
    Long id;
    String code;
    DiscounttypeDTO discountType;
    BigDecimal discountPercentage;
    ProductDTO product;
    CategoryDTO category;
    Integer quantity;
    LocalDate startDate;
    LocalDate endDate;
    Double minimumOrderValue;
    Double maximumDiscount;
    Boolean isActive;
}