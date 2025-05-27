package org.example.chuyendeweb_be.user.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private String phone;

}
