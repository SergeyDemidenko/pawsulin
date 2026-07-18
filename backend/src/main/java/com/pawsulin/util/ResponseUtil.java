package com.pawsulin.util;

import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtil {

    private static final Clock UTC_CLOCK = Clock.systemUTC();

    private ResponseUtil() {
    }

    public static Map<String, Object> buildErrorResponse(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now(UTC_CLOCK));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    public static Map<String, Object> buildValidationErrorResponse(String message, Map<String, String> errors, String path) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.BAD_REQUEST, message, path);
        body.put("errors", errors);
        return body;
    }
}
