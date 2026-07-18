package com.pawsulin.controller;

import com.pawsulin.dto.CreateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.UpdateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseAnalyticsDTO;
import com.pawsulin.service.GlucoseService;
import com.pawsulin.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pets/{petId}/glucose")
@Slf4j
public class GlucoseController {

    @Autowired
    private GlucoseService glucoseService;

    @PostMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseReadingDTO> createGlucoseReading(
            @PathVariable Long petId,
            @Valid @RequestBody CreateGlucoseReadingRequest request) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Create glucose reading request for pet: {} by user: {}", petId, userId);
        GlucoseReadingDTO dto = glucoseService.createGlucoseReading(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{readingId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseReadingDTO> getGlucoseReadingById(
            @PathVariable Long petId,
            @PathVariable Long readingId) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get glucose reading request for pet: {}, reading: {} by user: {}", petId, readingId, userId);
        GlucoseReadingDTO dto = glucoseService.getGlucoseReadingById(readingId, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<GlucoseReadingDTO>> getGlucoseReadings(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "readingTime") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get glucose readings request for pet: {} by user: {}", petId, userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<GlucoseReadingDTO> readings = glucoseService.getGlucoseReadingsByPetId(petId, userId, pageable);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<List<GlucoseReadingDTO>> getGlucoseReadingsByDateRange(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get glucose readings by date range for pet: {} from {} to {} by user: {}", petId, startTime, endTime, userId);
        List<GlucoseReadingDTO> readings = glucoseService.getGlucoseReadingsByDateRange(petId, userId, startTime, endTime);
        return ResponseEntity.ok(readings);
    }

    @PutMapping("/{readingId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseReadingDTO> updateGlucoseReading(
            @PathVariable Long petId,
            @PathVariable Long readingId,
            @Valid @RequestBody UpdateGlucoseReadingRequest request) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Update glucose reading request for pet: {}, reading: {} by user: {}", petId, readingId, userId);
        GlucoseReadingDTO dto = glucoseService.updateGlucoseReading(readingId, userId, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{readingId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteGlucoseReading(
            @PathVariable Long petId,
            @PathVariable Long readingId) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Delete glucose reading request for pet: {}, reading: {} by user: {}", petId, readingId, userId);
        glucoseService.deleteGlucoseReading(readingId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseAnalyticsDTO> getGlucoseAnalytics(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get glucose analytics for pet: {} from {} to {} by user: {}", petId, startTime, endTime, userId);
        GlucoseAnalyticsDTO analytics = glucoseService.getGlucoseAnalytics(petId, userId, startTime, endTime);
        return ResponseEntity.ok(analytics);
    }

    private Long extractUserIdFromAuthentication() {
        return SecurityUtil.getCurrentUserId();
    }
}
