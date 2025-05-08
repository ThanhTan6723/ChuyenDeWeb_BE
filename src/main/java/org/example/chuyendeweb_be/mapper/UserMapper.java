package org.example.chuyendeweb_be.mapper;

import org.example.chuyendeweb_be.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.dto.UserDTO;
import org.example.chuyendeweb_be.entity.User;
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
