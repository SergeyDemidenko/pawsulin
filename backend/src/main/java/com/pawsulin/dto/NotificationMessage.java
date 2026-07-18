package com.pawsulin.dto;

import com.pawsulin.entity.GlucoseAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WebSocket notification message sent to the client when a glucose alert threshold is breached.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {

    private String type;
    private String id;
    private Long petId;
    private String petName;
    private GlucoseAlert.AlertType alertType;
    private BigDecimal glucoseValue;
    private String message;
    private LocalDateTime triggeredAt;
}
