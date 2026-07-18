package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsulinLogDTO {
    private Long id;
    private Long petId;
    private Long userId;
    private String insulinType;
    private BigDecimal amountUnits;
    private LocalDateTime injectionTime;
    private String batchNumber;
    private LocalDate expirationDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
