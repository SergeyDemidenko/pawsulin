package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGlucoseReadingRequest {
    @NotNull(message = "Glucose value is required")
    @DecimalMin(value = "20", message = "Glucose value must be at least 20 mg/dL")
    private BigDecimal glucoseValue;

    @NotNull(message = "Reading time is required")
    private LocalDateTime readingTime;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
