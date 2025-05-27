package org.example.chuyendeweb_be.user.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscounttypeDTO implements Serializable {
    Long id;
    String type;
}