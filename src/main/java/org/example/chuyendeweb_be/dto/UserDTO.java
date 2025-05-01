package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.User}
 */
@Value
public class UserDTO implements Serializable {
    Long id;
    RoleDTO role;
    Integer failed;
    Boolean locked;
    Instant lockTime;
}