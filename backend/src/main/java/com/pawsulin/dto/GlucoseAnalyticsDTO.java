package com.pawsulin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlucoseAnalyticsDTO {
    private BigDecimal averageGlucose;
    private BigDecimal minGlucose;
    private BigDecimal maxGlucose;
    private Long readingsCount;
    private Long lowReadingsCount;
    private Long normalReadingsCount;
    private Long highReadingsCount;
    private Long criticalReadingsCount;
    private BigDecimal lowPercentage;
    private BigDecimal normalPercentage;
    private BigDecimal highPercentage;
    private BigDecimal criticalPercentage;
}
