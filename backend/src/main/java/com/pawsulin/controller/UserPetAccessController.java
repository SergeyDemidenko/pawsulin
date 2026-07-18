package com.pawsulin.controller;

import com.pawsulin.dto.UserPetAccessDTO;
import com.pawsulin.dto.UserPetAccessRequest;
import com.pawsulin.service.UserPetAccessService;
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
 * REST controller for managing shared pet access.
 * Exposes endpoints under {@code /api/v1/pets/{petId}/access} and {@code /api/v1/user/pets/access}.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserPetAccessController {

    @Autowired
    private UserPetAccessService userPetAccessService;

    /**
     * Grants access to a pet for another user.
     * Only the pet OWNER can call this endpoint.
     *
     * @param petId   path variable identifying the pet
     * @param request validated request body with targetUserId and accessLevel
     * @return {@code 201 Created} with the new {@link UserPetAccessDTO}
     */
    @PostMapping("/pets/{petId}/access")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<UserPetAccessDTO> grantAccess(
            @PathVariable Long petId,
            @Valid @RequestBody UserPetAccessRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Grant access request for pet: {} by user: {}", petId, userId);
        UserPetAccessDTO dto = userPetAccessService.grantAccess(petId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Returns a paginated list of all users with access to a pet.
     *
     * @param petId     path variable identifying the pet
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 10)
     * @param sortBy    field to sort by (default: createdAt)
     * @param direction sort direction (default: DESC)
     * @return {@code 200 OK} with a page of {@link UserPetAccessDTO}
     */
    @GetMapping("/pets/{petId}/access")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<UserPetAccessDTO>> getAccessByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get access records request for pet: {} by user: {}", petId, userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserPetAccessDTO> accessPage = userPetAccessService.getAccessByPetId(petId, userId, pageable);
        return ResponseEntity.ok(accessPage);
    }

    /**
     * Returns a specific access record by its identifier.
     *
     * @param petId    path variable identifying the pet (used for routing context)
     * @param accessId path variable identifying the access record
     * @return {@code 200 OK} with the matching {@link UserPetAccessDTO}
     */
    @GetMapping("/pets/{petId}/access/{accessId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<UserPetAccessDTO> getAccessById(
            @PathVariable Long petId,
            @PathVariable Long accessId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get access record: {} for pet: {} by user: {}", accessId, petId, userId);
        UserPetAccessDTO dto = userPetAccessService.getAccessById(accessId, userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Updates the access level for an existing access record.
     * Only the pet OWNER can call this endpoint.
     *
     * @param petId    path variable identifying the pet
     * @param accessId path variable identifying the access record to update
     * @param request  validated request body with new accessLevel
     * @return {@code 200 OK} with the updated {@link UserPetAccessDTO}
     */
    @PutMapping("/pets/{petId}/access/{accessId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<UserPetAccessDTO> updateAccessLevel(
            @PathVariable Long petId,
            @PathVariable Long accessId,
            @Valid @RequestBody UserPetAccessRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Update access level request for record: {} on pet: {} by user: {}", accessId, petId, userId);
        UserPetAccessDTO dto = userPetAccessService.updateAccessLevel(accessId, userId, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Revokes a target user's access to a pet.
     * Only the pet OWNER can call this endpoint.
     *
     * @param petId        path variable identifying the pet
     * @param targetUserId path variable identifying the user whose access is being revoked
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/pets/{petId}/access/{targetUserId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> revokeAccess(
            @PathVariable Long petId,
            @PathVariable Long targetUserId) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Revoke access request for target user: {} on pet: {} by user: {}", targetUserId, petId, userId);
        userPetAccessService.revokeAccess(petId, userId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns a paginated list of all pets the authenticated user has access to.
     *
     * @param page      zero-based page index (default: 0)
     * @param size      page size (default: 10)
     * @param sortBy    field to sort by (default: createdAt)
     * @param direction sort direction (default: DESC)
     * @return {@code 200 OK} with a page of {@link UserPetAccessDTO}
     */
    @GetMapping("/user/pets/access")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<UserPetAccessDTO>> getUserPetAccess(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("Get all pet access records for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<UserPetAccessDTO> accessPage = userPetAccessService.getAccessByUserId(userId, pageable);
        return ResponseEntity.ok(accessPage);
    }
}
