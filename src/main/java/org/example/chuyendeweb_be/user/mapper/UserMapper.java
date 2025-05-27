package org.example.chuyendeweb_be.user.mapper;

import org.example.chuyendeweb_be.user.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.user.dto.UserDTO;
import org.example.chuyendeweb_be.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequestDTO dto);
    User toEntity(UserDTO dto);
    UserDTO toDto(User user);



}
