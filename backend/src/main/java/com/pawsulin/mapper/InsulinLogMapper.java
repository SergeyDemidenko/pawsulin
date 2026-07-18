package com.pawsulin.mapper;

import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.entity.InsulinLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InsulinLogMapper {

    @Mapping(target = "petId", source = "pet.id")
    @Mapping(target = "userId", source = "user.id")
    InsulinLogDTO toDTO(InsulinLog insulinLog);
}
