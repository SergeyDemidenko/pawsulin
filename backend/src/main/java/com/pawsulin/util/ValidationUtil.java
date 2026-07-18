package com.pawsulin.util;

import com.pawsulin.exception.InvalidRequestException;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static <T> T requireFieldNonNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return value;
    }

    public static String requireHasText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidRequestException(fieldName + " must not be blank");
        }
        return value;
    }

    public static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new InvalidRequestException(fieldName + " must be greater than or equal to 0");
        }
        return value;
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidRequestException(fieldName + " must be greater than 0");
        }
        return value;
    }
}
