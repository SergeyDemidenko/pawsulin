package com.pawsulin.util;

import com.pawsulin.exception.InvalidRequestException;

import java.time.LocalDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static DateTimeRange validateAndCreateRange(LocalDateTime startTime, LocalDateTime endTime) {
        ValidationUtil.requireNonNull(startTime, "startTime");
        ValidationUtil.requireNonNull(endTime, "endTime");
        if (startTime.isAfter(endTime)) {
            throw new InvalidRequestException("startTime must not be after endTime");
        }
        return new DateTimeRange(startTime, endTime);
    }

    public record DateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
