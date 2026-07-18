package com.pawsulin.mapper;

import com.pawsulin.dto.GlucoseAlertDTO;
import com.pawsulin.entity.GlucoseAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GlucoseAlertMapper {

    @Mapping(target = "petId", source = "pet.id")
    @Mapping(target = "userId", source = "user.id")
    GlucoseAlertDTO toDTO(GlucoseAlert glucoseAlert);
}
