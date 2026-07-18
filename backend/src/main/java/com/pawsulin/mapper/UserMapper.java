package com.pawsulin.mapper;

import com.pawsulin.dto.UserDTO;
import com.pawsulin.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserDTO toDTO(User user);

    default String mapRoleToString(User.UserRole role) {
        return role != null ? role.name() : null;
    }
}
