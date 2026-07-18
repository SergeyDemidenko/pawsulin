package com.pawsulin.dto;

import com.pawsulin.entity.GlucoseAlert;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new glucose alert configuration.
 * Contains JSR-303 validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGlucoseAlertRequest {

    @NotNull(message = "Alert type is required")
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

    /**
     * Validates that low threshold is less than high threshold when both are provided.
     *
     * @return {@code true} if thresholds are valid or not both provided
     */
    @jakarta.validation.constraints.AssertTrue(message = "Low threshold must be less than high threshold")
    public boolean isThresholdRangeValid() {
        if (lowThreshold == null || highThreshold == null) {
            return true;
        }
        return lowThreshold.compareTo(highThreshold) < 0;
    }
}
