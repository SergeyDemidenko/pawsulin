package com.pawsulin.dto;

import com.pawsulin.entity.GlucoseAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data transfer object for glucose alert configuration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlucoseAlertDTO {
    private Long id;
    private Long petId;
    private Long userId;
    private GlucoseAlert.AlertType alertType;
    private BigDecimal lowThreshold;
    private BigDecimal highThreshold;
    private Boolean isEnabled;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
