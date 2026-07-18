package com.pawsulin.mapper;

import com.pawsulin.dto.UserDTO;
import com.pawsulin.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
}
