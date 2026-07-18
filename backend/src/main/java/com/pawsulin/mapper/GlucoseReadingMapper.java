package com.pawsulin.mapper;

import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.entity.GlucoseReading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GlucoseReadingMapper {

    @Mapping(target = "petId", source = "pet.id")
    @Mapping(target = "userId", source = "user.id")
    GlucoseReadingDTO toDTO(GlucoseReading glucoseReading);
}
