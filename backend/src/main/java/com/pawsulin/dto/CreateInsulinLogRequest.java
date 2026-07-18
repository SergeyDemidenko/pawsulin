package com.pawsulin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a new insulin log entry.
 * Contains JSR-303 validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInsulinLogRequest {

    @NotBlank(message = "Insulin type is required")
    @Size(max = 100, message = "Insulin type must not exceed 100 characters")
    private String insulinType;

    @NotNull(message = "Amount in units is required")
    @DecimalMin(value = "0.1", message = "Amount must be at least 0.1 units")
    private BigDecimal amountUnits;

    @NotNull(message = "Injection time is required")
    private LocalDateTime injectionTime;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    private LocalDate expirationDate;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
