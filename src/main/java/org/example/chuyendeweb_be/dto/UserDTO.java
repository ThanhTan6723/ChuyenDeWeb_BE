package org.example.chuyendeweb_be.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    Long id;
    RoleDTO role;
    Integer failed;
    Boolean locked;
    Instant lockTime;
}