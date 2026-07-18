package com.pawsulin.mapper;

import com.pawsulin.dto.UserPetAccessDTO;
import com.pawsulin.entity.UserPetAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPetAccessMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "petId", source = "pet.id")
    UserPetAccessDTO toDTO(UserPetAccess userPetAccess);
}
