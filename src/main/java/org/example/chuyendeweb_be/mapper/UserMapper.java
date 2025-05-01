package org.example.chuyendeweb_be.mapper;

import org.example.chuyendeweb_be.dto.UserDTO;
import org.example.chuyendeweb_be.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO dto);
    UserDTO toDto(User user);
}
