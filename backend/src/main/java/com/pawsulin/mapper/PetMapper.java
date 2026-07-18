package com.pawsulin.mapper;

import com.pawsulin.dto.PetDTO;
import com.pawsulin.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "userId", source = "user.id")
    PetDTO toDTO(Pet pet);
}
