package com.pawsulin.dto;

import com.pawsulin.entity.GlucoseAlert;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing glucose alert configuration.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGlucoseAlertRequest {

    private GlucoseAlert.AlertType alertType;

    @DecimalMin(value = "0.0", message = "Low threshold must be non-negative")
    @DecimalMax(value = "999.99", message = "Low threshold must not exceed 999.99")
    private BigDecimal lowThreshold;

    @DecimalMin(value = "0.0", message = "High threshold must be non-negative")
    @DecimalMax(value = "999.99", message = "High threshold must not exceed 999.99")
    private BigDecimal highThreshold;

    private Boolean isEnabled;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
