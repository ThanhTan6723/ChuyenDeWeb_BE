package org.example.chuyendeweb_be.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;

}
