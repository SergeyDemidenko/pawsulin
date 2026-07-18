package com.pawsulin.util;

import com.pawsulin.exception.InvalidRequestException;

import java.time.LocalDateTime;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static DateTimeRange validateAndCreateRange(LocalDateTime startTime, LocalDateTime endTime) {
        ValidationUtil.requireFieldNonNull(startTime, "startTime");
        ValidationUtil.requireFieldNonNull(endTime, "endTime");
        if (startTime.isAfter(endTime)) {
            throw new InvalidRequestException("startTime must not be after endTime");
        }
        return new DateTimeRange(startTime, endTime);
    }

    public record DateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
