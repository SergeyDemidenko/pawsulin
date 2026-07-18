package com.pawsulin.service;

import com.pawsulin.dto.UserPetAccessDTO;
import com.pawsulin.dto.UserPetAccessRequest;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.entity.UserPetAccess;
import com.pawsulin.exception.AccessDeniedException;
import com.pawsulin.exception.DuplicateResourceException;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.UserPetAccessMapper;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserPetAccessRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for managing shared pet access across users.
 * Implements full CRUD operations with access level enforcement
 * (OWNER, EDITOR, VIEWER) and pagination support.
 */
@Service
@Slf4j
@Transactional
public class UserPetAccessService {

    @Autowired
    private UserPetAccessRepository userPetAccessRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPetAccessMapper userPetAccessMapper;

    /**
     * Grants access to a pet for another user.
     * Only the pet OWNER may grant access; the owner cannot grant access to themselves;
     * and duplicate access records are rejected.
     *
     * @param petId   the pet identifier
     * @param userId  the authenticated user (must be OWNER of the pet)
     * @param request contains targetUserId and desired accessLevel
     * @return the created {@link UserPetAccessDTO}
     * @throws ResourceNotFoundException  if pet or target user is not found
     * @throws AccessDeniedException      if the caller is not the pet OWNER or tries to grant OWNER level
     * @throws IllegalArgumentException   if the caller tries to grant access to themselves
     * @throws DuplicateResourceException if access already exists for the target user
     */
    public UserPetAccessDTO grantAccess(Long petId, Long userId, UserPetAccessRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        // Verify caller is the OWNER of this pet
        UserPetAccess callerAccess = userPetAccessRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to pet with id: " + petId));

        if (callerAccess.getAccessLevel() != UserPetAccess.AccessLevel.OWNER) {
            log.warn("Non-owner user: {} attempted to grant access to pet: {}", userId, petId);
            throw new AccessDeniedException("Only the pet owner can grant access to other users");
        }

        // Prevent granting OWNER access (only one owner per pet)
        if (request.getAccessLevel() == UserPetAccess.AccessLevel.OWNER) {
            throw new AccessDeniedException("Cannot grant OWNER access level; there can only be one owner per pet");
        }

        Long targetUserId = request.getTargetUserId();

        // Prevent self-access grants
        if (userId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot grant access to yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        // Check for duplicate access
        if (userPetAccessRepository.existsByUserIdAndPetId(targetUserId, petId)) {
            throw new DuplicateResourceException(
                    "User " + targetUserId + " already has access to pet " + petId);
        }

        UserPetAccess access = UserPetAccess.builder()
                .user(targetUser)
                .pet(pet)
                .accessLevel(request.getAccessLevel())
                .build();

        access = userPetAccessRepository.save(access);
        log.info("Access granted: user {} granted {} access to pet {} for user {}",
                userId, request.getAccessLevel(), petId, targetUserId);
        return userPetAccessMapper.toDTO(access);
    }

    /**
     * Returns a paginated list of all access records for a given pet.
     * The caller must have at least VIEWER access to the pet.
     *
     * @param petId    the pet identifier
     * @param userId   the authenticated user identifier
     * @param pageable pagination parameters
     * @return paginated {@link UserPetAccessDTO} results
     * @throws ResourceNotFoundException if pet is not found
     * @throws AccessDeniedException     if the user does not have access to the pet
     */
    @Transactional(readOnly = true)
    public Page<UserPetAccessDTO> getAccessByPetId(Long petId, Long userId, Pageable pageable) {
        petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        // Only users with any access level can list access records
        userPetAccessRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to pet with id: " + petId));

        Page<UserPetAccess> accessPage = userPetAccessRepository.findByPetId(petId, pageable);
        log.info("Retrieved {} access records for pet: {}", accessPage.getTotalElements(), petId);
        return accessPage.map(userPetAccessMapper::toDTO);
    }

    /**
     * Returns a paginated list of all pets a given user has access to.
     *
     * @param userId   the user whose access records are being listed
     * @param pageable pagination parameters
     * @return paginated {@link UserPetAccessDTO} results
     */
    @Transactional(readOnly = true)
    public Page<UserPetAccessDTO> getAccessByUserId(Long userId, Pageable pageable) {
        Page<UserPetAccess> accessPage = userPetAccessRepository.findByUserId(userId, pageable);
        log.info("Retrieved {} access records for user: {}", accessPage.getTotalElements(), userId);
        return accessPage.map(userPetAccessMapper::toDTO);
    }

    /**
     * Retrieves a specific access record by its identifier.
     * The caller must have access to the pet the record belongs to.
     *
     * @param accessId the access record identifier
     * @param userId   the authenticated user identifier
     * @return the matching {@link UserPetAccessDTO}
     * @throws ResourceNotFoundException if the access record is not found
     * @throws AccessDeniedException     if the caller has no access to the pet
     */
    @Transactional(readOnly = true)
    public UserPetAccessDTO getAccessById(Long accessId, Long userId) {
        UserPetAccess access = userPetAccessRepository.findById(accessId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Access record not found with id: " + accessId));

        // Caller must have any access to this pet
        userPetAccessRepository.findByUserIdAndPetId(userId, access.getPet().getId())
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to pet with id: " + access.getPet().getId()));

        return userPetAccessMapper.toDTO(access);
    }

    /**
     * Updates the access level for an existing access record.
     * Only the pet OWNER may update access levels;
     * the OWNER's own access record cannot be modified.
     *
     * @param accessId the access record identifier to update
     * @param userId   the authenticated user (must be OWNER of the pet)
     * @param request  contains the new accessLevel (targetUserId is ignored here)
     * @return the updated {@link UserPetAccessDTO}
     * @throws ResourceNotFoundException if the access record is not found
     * @throws AccessDeniedException     if the caller is not OWNER, or tries to change OWNER record, or tries to set OWNER level
     */
    public UserPetAccessDTO updateAccessLevel(Long accessId, Long userId, UserPetAccessRequest request) {
        UserPetAccess access = userPetAccessRepository.findById(accessId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Access record not found with id: " + accessId));

        Long petId = access.getPet().getId();

        // Caller must be OWNER of this pet
        UserPetAccess callerAccess = userPetAccessRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to pet with id: " + petId));

        if (callerAccess.getAccessLevel() != UserPetAccess.AccessLevel.OWNER) {
            log.warn("Non-owner user: {} attempted to update access record: {}", userId, accessId);
            throw new AccessDeniedException("Only the pet owner can update access levels");
        }

        // OWNER access record cannot be changed
        if (access.getAccessLevel() == UserPetAccess.AccessLevel.OWNER) {
            throw new AccessDeniedException("Cannot change the OWNER's access level");
        }

        // New level cannot be OWNER (only one owner per pet)
        if (request.getAccessLevel() == UserPetAccess.AccessLevel.OWNER) {
            throw new AccessDeniedException("Cannot set OWNER access level; there can only be one owner per pet");
        }

        access.setAccessLevel(request.getAccessLevel());
        access = userPetAccessRepository.save(access);
        log.info("Access level updated: record {} set to {} by user {}", accessId, request.getAccessLevel(), userId);
        return userPetAccessMapper.toDTO(access);
    }

    /**
     * Revokes a target user's access to a pet.
     * Only the pet OWNER may revoke access;
     * the OWNER cannot revoke their own access (that would remove the only owner).
     *
     * @param petId        the pet identifier
     * @param userId       the authenticated user (must be OWNER of the pet)
     * @param targetUserId the user whose access should be revoked
     * @throws ResourceNotFoundException if pet or target access record is not found
     * @throws AccessDeniedException     if caller is not OWNER, or tries to revoke OWNER access
     */
    public void revokeAccess(Long petId, Long userId, Long targetUserId) {
        petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        // Caller must be OWNER of this pet
        UserPetAccess callerAccess = userPetAccessRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to pet with id: " + petId));

        if (callerAccess.getAccessLevel() != UserPetAccess.AccessLevel.OWNER) {
            log.warn("Non-owner user: {} attempted to revoke access to pet: {}", userId, petId);
            throw new AccessDeniedException("Only the pet owner can revoke access");
        }

        UserPetAccess targetAccess = userPetAccessRepository.findByUserIdAndPetId(targetUserId, petId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Access record not found for user " + targetUserId + " on pet " + petId));

        // Cannot revoke the OWNER's own access
        if (targetAccess.getAccessLevel() == UserPetAccess.AccessLevel.OWNER) {
            throw new AccessDeniedException("Cannot revoke the pet owner's access");
        }

        userPetAccessRepository.delete(targetAccess);
        log.info("Access revoked: user {} revoked access to pet {} for user {}", userId, petId, targetUserId);
    }

    /**
     * Checks whether a user has any access to the specified pet.
     *
     * @param userId the user identifier
     * @param petId  the pet identifier
     * @return {@code true} if the user has an access record for the pet
     */
    @Transactional(readOnly = true)
    public boolean hasAccess(Long userId, Long petId) {
        return userPetAccessRepository.existsByUserIdAndPetId(userId, petId);
    }

    /**
     * Returns the access level a user has for the specified pet.
     *
     * @param userId the user identifier
     * @param petId  the pet identifier
     * @return an {@link Optional} containing the {@link UserPetAccess.AccessLevel}, or empty if no access
     */
    @Transactional(readOnly = true)
    public Optional<UserPetAccess.AccessLevel> getAccessLevel(Long userId, Long petId) {
        return userPetAccessRepository.findByUserIdAndPetId(userId, petId)
                .map(UserPetAccess::getAccessLevel);
    }

}
