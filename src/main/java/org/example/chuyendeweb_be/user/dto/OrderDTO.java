package org.example.chuyendeweb_be.user.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private String consigneeName;
    private String consigneePhone;
    private String address;
    private String orderNotes;
    private List<OrderDetailDTO> orderDetails;
    private BigDecimal ship;
    private BigDecimal discountValue;
}