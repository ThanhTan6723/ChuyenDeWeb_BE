package org.example.chuyendeweb_be.user.dto;

import lombok.*;

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