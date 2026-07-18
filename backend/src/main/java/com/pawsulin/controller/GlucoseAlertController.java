package com.pawsulin.controller;

import com.pawsulin.dto.CreateGlucoseAlertRequest;
import com.pawsulin.dto.GlucoseAlertDTO;
import com.pawsulin.dto.UpdateGlucoseAlertRequest;
import com.pawsulin.service.GlucoseAlertService;
import com.pawsulin.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for glucose alert management.
 * Exposes endpoints under {@code /api/v1/pets/{petId}/alerts} and {@code /api/v1/alerts}.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class GlucoseAlertController {

    @Autowired
    private GlucoseAlertService glucoseAlertService;

    /**
     * Creates a new glucose alert for the specified pet.
     *
     * @param petId   path variable identifying the pet
     * @param request validated request body with alert details
     * @return {@code 201 Created} with the new {@link GlucoseAlertDTO}
     */
    @PostMapping("/pets/{petId}/alerts")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseAlertDTO> createGlucoseAlert(
            @PathVariable Long petId,
            @Valid @RequestBody CreateGlucoseAlertRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Create glucose alert request for pet: {} by user: {}", petId, userId);
        GlucoseAlertDTO dto = glucoseAlertService.createGlucoseAlert(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Retrieves a specific glucose alert by its identifier.
     *
     * @param petId   path variable identifying the pet
     * @param alertId path variable identifying the alert
     * @return {@code 200 OK} with the matching {@link GlucoseAlertDTO}
     */
    @GetMapping("/pets/{petId}/alerts/{alertId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseAlertDTO> getGlucoseAlertById(
            @PathVariable Long petId,
            @PathVariable Long alertId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get glucose alert request for pet: {}, alert: {} by user: {}", petId, alertId, userId);
        GlucoseAlertDTO dto = glucoseAlertService.getGlucoseAlertById(alertId, userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Returns a paginated list of enabled glucose alerts for a pet.
     *
     * @param petId     path variable identifying the pet
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 10)
     * @param sortBy    field to sort by (default: createdAt)
     * @param direction sort direction (default: DESC)
     * @return {@code 200 OK} with a page of {@link GlucoseAlertDTO}
     */
    @GetMapping("/pets/{petId}/alerts")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<GlucoseAlertDTO>> getAlertsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get glucose alerts request for pet: {} by user: {}", petId, userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<GlucoseAlertDTO> alerts = glucoseAlertService.getAlertsByPetId(petId, userId, pageable);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Returns a paginated list of all enabled glucose alerts for the authenticated user.
     *
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 10)
     * @param sortBy    field to sort by (default: createdAt)
     * @param direction sort direction (default: DESC)
     * @return {@code 200 OK} with a page of {@link GlucoseAlertDTO}
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<GlucoseAlertDTO>> getAlertsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get all glucose alerts for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<GlucoseAlertDTO> alerts = glucoseAlertService.getAlertsByUserId(userId, pageable);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Updates an existing glucose alert (partial update supported).
     *
     * @param petId   path variable identifying the pet
     * @param alertId path variable identifying the alert
     * @param request validated request body with fields to update
     * @return {@code 200 OK} with the updated {@link GlucoseAlertDTO}
     */
    @PutMapping("/pets/{petId}/alerts/{alertId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<GlucoseAlertDTO> updateGlucoseAlert(
            @PathVariable Long petId,
            @PathVariable Long alertId,
            @Valid @RequestBody UpdateGlucoseAlertRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Update glucose alert request for pet: {}, alert: {} by user: {}", petId, alertId, userId);
        GlucoseAlertDTO dto = glucoseAlertService.updateGlucoseAlert(alertId, userId, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Soft-deletes a glucose alert.
     *
     * @param petId   path variable identifying the pet
     * @param alertId path variable identifying the alert
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/pets/{petId}/alerts/{alertId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deleteGlucoseAlert(
            @PathVariable Long petId,
            @PathVariable Long alertId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Delete glucose alert request for pet: {}, alert: {} by user: {}", petId, alertId, userId);
        glucoseAlertService.deleteGlucoseAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }
}
