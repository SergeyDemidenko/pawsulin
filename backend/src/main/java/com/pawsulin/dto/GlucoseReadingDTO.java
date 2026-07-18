package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlucoseReadingDTO {
    private Long id;
    private Long petId;
    private Long userId;
    private BigDecimal glucoseValue;
    private String glucoseLevel;
    private LocalDateTime readingTime;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
