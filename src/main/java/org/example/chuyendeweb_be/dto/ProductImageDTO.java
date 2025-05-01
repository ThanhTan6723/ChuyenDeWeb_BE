package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO implements Serializable {
    Long id;
    ImageDTO image;
}