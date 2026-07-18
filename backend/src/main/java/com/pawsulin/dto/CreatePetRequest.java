package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePetRequest {
    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 100, message = "Pet name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(min = 2, max = 50, message = "Species must be between 2 and 50 characters")
    private String species;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @NotNull(message = "Age is required")
    private Integer ageYears;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private BigDecimal weightKg;

    @NotBlank(message = "Diabetes type is required")
    @Size(min = 2, max = 50, message = "Diabetes type must be between 2 and 50 characters")
    private String diabetesType;

    private String medicalNotes;
}
