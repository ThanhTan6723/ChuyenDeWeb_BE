package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "orderdetail")
public class Orderdetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_price", precision = 10, scale = 3)
    private BigDecimal productPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "priceWithQuantity", precision = 10, scale = 3)
    private BigDecimal priceWithQuantity;

}