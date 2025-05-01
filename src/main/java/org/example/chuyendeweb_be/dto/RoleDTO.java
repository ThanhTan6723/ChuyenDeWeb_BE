package org.example.chuyendeweb_be.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.example.chuyendeweb_be.entity.Role}
 */
@Value
public class RoleDTO implements Serializable {
    Long id;
    String roleName;
}