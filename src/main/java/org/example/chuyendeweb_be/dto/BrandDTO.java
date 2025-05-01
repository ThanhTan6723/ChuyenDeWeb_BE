package org.example.chuyendeweb_be.dto;

import lombok.*;
import org.example.chuyendeweb_be.entity.Brand;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO implements Serializable {
    Long id;
    String name;
    String address;
}