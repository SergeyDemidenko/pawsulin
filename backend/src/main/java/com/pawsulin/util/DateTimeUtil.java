package com.pawsulin.util;

import java.time.LocalDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static DateTimeRange validateAndCreateRange(LocalDateTime startTime, LocalDateTime endTime) {
        ValidationUtil.requireNonNull(startTime, "startTime");
        ValidationUtil.requireNonNull(endTime, "endTime");
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime must be before or equal to endTime");
        }
        return new DateTimeRange(startTime, endTime);
    }

    public record DateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
