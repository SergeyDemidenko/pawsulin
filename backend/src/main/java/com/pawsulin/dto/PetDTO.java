package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDTO {
    private Long id;
    private Long userId;
    private String name;
    private String species;
    private String breed;
    private Integer ageYears;
    private BigDecimal weightKg;
    private String diabetesType;
    private String medicalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
