package org.example.chuyendeweb_be.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String password;
//    private String repassword;
    private String email;
    private String phonenumber;

}
