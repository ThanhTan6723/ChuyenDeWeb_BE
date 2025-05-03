package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="batch")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date manufacturingDate;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Temporal(TemporalType.DATE)
    private Date dateOfImporting;

    private int quantity;
    private int currentQuantity;
    private double priceImport;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User adminCreate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
