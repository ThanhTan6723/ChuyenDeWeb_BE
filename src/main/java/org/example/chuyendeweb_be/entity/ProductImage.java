package org.example.chuyendeweb_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "productimage")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;
    private boolean isMain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imageId")
    private Image image;

}