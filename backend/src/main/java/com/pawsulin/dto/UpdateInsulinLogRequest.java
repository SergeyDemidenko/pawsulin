package com.pawsulin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing insulin log entry.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInsulinLogRequest {

    @Size(max = 100, message = "Insulin type must not exceed 100 characters")
    private String insulinType;

    @DecimalMin(value = "0.1", message = "Amount must be at least 0.1 units")
    private BigDecimal amountUnits;

    private LocalDateTime injectionTime;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    private LocalDate expirationDate;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
