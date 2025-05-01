package org.example.chuyendeweb_be.dto;

import lombok.Value;
import org.example.chuyendeweb_be.entity.ProductDto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Batch}
 */
@Value
public class BatchDTO implements Serializable {
    Long id;
    String name;
    Date manufacturingDate;
    Date expiryDate;
    Date dateOfImporting;
    int quantity;
    int currentQuantity;
    double priceImport;
    BrandDTO provider;
    ProductDto product;
}