package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePetRequest {
    @Size(min = 2, max = 100, message = "Pet name must be between 2 and 100 characters")
    private String name;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    private Integer ageYears;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private BigDecimal weightKg;

    @Size(min = 2, max = 50, message = "Diabetes type must be between 2 and 50 characters")
    private String diabetesType;

    private String medicalNotes;
}
