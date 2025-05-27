package org.example.chuyendeweb_be.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginRequestDTO {
    private String email;
    private String phone;
    private String password;

}
