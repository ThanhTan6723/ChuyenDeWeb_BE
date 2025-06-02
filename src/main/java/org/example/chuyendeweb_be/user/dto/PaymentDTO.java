package org.example.chuyendeweb_be.user.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO implements Serializable {
    private Long id;
    private String methodName;
}