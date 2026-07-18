package com.pawsulin.mapper;

import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.entity.GlucoseReading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GlucoseReadingMapper {

    @Mapping(target = "petId", source = "pet.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "glucoseLevel", source = "glucoseLevel")
    GlucoseReadingDTO toDTO(GlucoseReading glucoseReading);

    default String mapGlucoseLevelToString(GlucoseReading.GlucoseLevel glucoseLevel) {
        return glucoseLevel != null ? glucoseLevel.name() : null;
    }
}
