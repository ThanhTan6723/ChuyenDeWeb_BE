package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_date")
    private Instant bookingDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "consignee_name", length = 30)
    private String consigneeName;

    @Column(name = "consignee_phone", length = 15)
    private String consigneePhone;

    @Column(name = "ship", precision = 15, scale = 3)
    private BigDecimal ship;

    @Column(name = "discountValue", precision = 15, scale = 3)
    private BigDecimal discountValue;

    @Column(name = "totalMoney", precision = 15, scale = 3)
    private BigDecimal totalMoney;

    @Column(name = "address")
    private String address;

    @Column(name = "orderNotes")
    private String orderNotes;

    @Column(name = "orderStatus")
    private String orderStatus;

}