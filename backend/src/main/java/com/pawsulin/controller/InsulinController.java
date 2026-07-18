package com.pawsulin.controller;

import com.pawsulin.dto.CreateInsulinLogRequest;
import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.dto.UpdateInsulinLogRequest;
import com.pawsulin.service.InsulinService;
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

/**
 * REST controller for insulin log management.
 * Exposes endpoints under {@code /api/v1/pets/{petId}/insulin}.
 */
@RestController
@RequestMapping("/api/v1/pets/{petId}/insulin")
@Slf4j
public class InsulinController {

    @Autowired
    private InsulinService insulinService;

    /**
     * Creates a new insulin log entry for the specified pet.
     *
     * @param petId   path variable identifying the pet
     * @param request validated request body with insulin details
     * @return {@code 201 Created} with the new {@link InsulinLogDTO}
     */
    @PostMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<InsulinLogDTO> createInsulinLog(
            @PathVariable Long petId,
            @Valid @RequestBody CreateInsulinLogRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Create insulin log request for pet: {} by user: {}", petId, userId);
        InsulinLogDTO dto = insulinService.createInsulinLog(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Retrieves a specific insulin log by its identifier.
     *
     * @param petId path variable identifying the pet
     * @param logId path variable identifying the insulin log
     * @return {@code 200 OK} with the matching {@link InsulinLogDTO}
     */
    @GetMapping("/{logId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<InsulinLogDTO> getInsulinLogById(
            @PathVariable Long petId,
            @PathVariable Long logId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get insulin log request for pet: {}, log: {} by user: {}", petId, logId, userId);
        InsulinLogDTO dto = insulinService.getInsulinLogById(logId, userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Returns a paginated list of active insulin logs for a pet.
     *
     * @param petId     path variable identifying the pet
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 10)
     * @param sortBy    field to sort by (default: injectionTime)
     * @param direction sort direction (default: DESC)
     * @return {@code 200 OK} with a page of {@link InsulinLogDTO}
     */
    @GetMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<InsulinLogDTO>> getInsulinLogs(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "injectionTime") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get insulin logs request for pet: {} by user: {}", petId, userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<InsulinLogDTO> logs = insulinService.getInsulinLogsByPetId(petId, userId, pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * Returns insulin logs for a pet filtered by injection time range.
     *
     * @param petId     path variable identifying the pet
     * @param startTime start of the date range (ISO 8601)
     * @param endTime   end of the date range (ISO 8601)
     * @return {@code 200 OK} with a list of {@link InsulinLogDTO}
     */
    @GetMapping("/range")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<List<InsulinLogDTO>> getInsulinLogsByDateRange(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get insulin logs by date range for pet: {} from {} to {} by user: {}", petId, startTime, endTime, userId);
        List<InsulinLogDTO> logs = insulinService.getInsulinLogsByDateRange(petId, userId, startTime, endTime);
        return ResponseEntity.ok(logs);
    }

    /**
     * Updates an existing insulin log (partial update supported).
     *
     * @param petId   path variable identifying the pet
     * @param logId   path variable identifying the insulin log
     * @param request validated request body with fields to update
     * @return {@code 200 OK} with the updated {@link InsulinLogDTO}
     */
    @PutMapping("/{logId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<InsulinLogDTO> updateInsulinLog(
            @PathVariable Long petId,
            @PathVariable Long logId,
            @Valid @RequestBody UpdateInsulinLogRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Update insulin log request for pet: {}, log: {} by user: {}", petId, logId, userId);
        InsulinLogDTO dto = insulinService.updateInsulinLog(logId, userId, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Soft-deletes an insulin log.
     *
     * @param petId path variable identifying the pet
     * @param logId path variable identifying the insulin log
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{logId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteInsulinLog(
            @PathVariable Long petId,
            @PathVariable Long logId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Delete insulin log request for pet: {}, log: {} by user: {}", petId, logId, userId);
        insulinService.deleteInsulinLog(logId, userId);
        return ResponseEntity.noContent().build();
    }
}
