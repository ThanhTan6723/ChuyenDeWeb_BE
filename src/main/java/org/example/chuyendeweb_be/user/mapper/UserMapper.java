package org.example.chuyendeweb_be.user.mapper;

import org.example.chuyendeweb_be.user.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.user.dto.RoleDTO;
import org.example.chuyendeweb_be.user.dto.UserDTO;
import org.example.chuyendeweb_be.user.entity.Role;
import org.example.chuyendeweb_be.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "role", ignore = true)
//    User toEntity(RegisterRequestDTO dto);
//    User toEntity(UserDTO dto);
//    UserDTO toDto(User user);
@Mapping(target = "id", ignore = true)
@Mapping(target = "role", ignore = true)
User toEntity(RegisterRequestDTO dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    User toEntity(UserDTO dto);

    UserDTO toDto(User user);

    RoleDTO toRoleDto(Role role);

    Role toRoleEntity(RoleDTO roleDTO);



}
