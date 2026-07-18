package com.pawsulin.service;

import com.pawsulin.dto.CreateGlucoseAlertRequest;
import com.pawsulin.dto.GlucoseAlertDTO;
import com.pawsulin.dto.UpdateGlucoseAlertRequest;
import com.pawsulin.entity.GlucoseAlert;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.GlucoseAlertMapper;
import com.pawsulin.repository.GlucoseAlertRepository;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing glucose alert configurations.
 * Implements full CRUD with pagination, threshold checking, and soft delete via {@code isEnabled}.
 */
@Service
@Slf4j
@Transactional
public class GlucoseAlertService {

    @Autowired
    private GlucoseAlertRepository glucoseAlertRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlucoseAlertMapper glucoseAlertMapper;

    /**
     * Creates a new glucose alert for a pet.
     *
     * @param petId   the pet identifier
     * @param userId  the authenticated user identifier
     * @param request the create request with alert details
     * @return the created {@link GlucoseAlertDTO}
     * @throws ResourceNotFoundException if pet or user not found, or user is unauthorized
     */
    public GlucoseAlertDTO createGlucoseAlert(Long petId, Long userId, CreateGlucoseAlertRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized alert creation for pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        GlucoseAlert alert = GlucoseAlert.builder()
                .pet(pet)
                .user(user)
                .alertType(request.getAlertType())
                .lowThreshold(request.getLowThreshold())
                .highThreshold(request.getHighThreshold())
                .isEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true)
                .description(request.getDescription())
                .build();

        alert = glucoseAlertRepository.save(alert);
        log.info("Glucose alert created successfully: {} for pet: {}", alert.getId(), petId);
        return glucoseAlertMapper.toDTO(alert);
    }

    /**
     * Retrieves a single glucose alert by its identifier.
     *
     * @param alertId the glucose alert identifier
     * @param userId  the authenticated user identifier
     * @return the matching {@link GlucoseAlertDTO}
     * @throws ResourceNotFoundException if the alert is not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public GlucoseAlertDTO getGlucoseAlertById(Long alertId, Long userId) {
        GlucoseAlert alert = glucoseAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose alert not found with id: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to glucose alert: {} by user: {}", alertId, userId);
            throw new ResourceNotFoundException("Glucose alert not found or unauthorized access");
        }

        return glucoseAlertMapper.toDTO(alert);
    }

    /**
     * Returns a paginated list of enabled alerts for a pet.
     *
     * @param petId    the pet identifier
     * @param userId   the authenticated user identifier
     * @param pageable pagination parameters
     * @return paginated {@link GlucoseAlertDTO} results
     * @throws ResourceNotFoundException if pet not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public Page<GlucoseAlertDTO> getAlertsByPetId(Long petId, Long userId, Pageable pageable) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        Page<GlucoseAlert> alerts = glucoseAlertRepository.findByPetIdAndIsEnabledTrue(petId, pageable);
        log.info("Retrieved {} glucose alerts for pet: {}", alerts.getTotalElements(), petId);
        return alerts.map(glucoseAlertMapper::toDTO);
    }

    /**
     * Returns a paginated list of all enabled alerts belonging to a user.
     *
     * @param userId   the authenticated user identifier
     * @param pageable pagination parameters
     * @return paginated {@link GlucoseAlertDTO} results
     */
    @Transactional(readOnly = true)
    public Page<GlucoseAlertDTO> getAlertsByUserId(Long userId, Pageable pageable) {
        Page<GlucoseAlert> alerts = glucoseAlertRepository.findByUserIdAndIsEnabledTrue(userId, pageable);
        log.info("Retrieved {} glucose alerts for user: {}", alerts.getTotalElements(), userId);
        return alerts.map(glucoseAlertMapper::toDTO);
    }

    /**
     * Returns only the enabled alerts for a pet (non-paginated).
     *
     * @param petId  the pet identifier
     * @param userId the authenticated user identifier
     * @return list of enabled {@link GlucoseAlertDTO}
     * @throws ResourceNotFoundException if pet not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public List<GlucoseAlertDTO> getEnabledAlerts(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        return glucoseAlertRepository.findByPetIdAndIsEnabledTrue(petId).stream()
                .map(glucoseAlertMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Checks whether a glucose reading triggers any enabled alerts for a pet and returns
     * the list of triggered alerts.
     *
     * @param petId        the pet identifier
     * @param userId       the authenticated user identifier
     * @param glucoseValue the current glucose reading value (mg/dL)
     * @return list of triggered {@link GlucoseAlertDTO}
     * @throws ResourceNotFoundException if pet not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public List<GlucoseAlertDTO> checkAlertThresholds(Long petId, Long userId, BigDecimal glucoseValue) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized threshold check for pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        BigDecimal value = glucoseValue;
        List<GlucoseAlert> enabledAlerts = glucoseAlertRepository.findByPetIdAndIsEnabledTrue(petId);

        List<GlucoseAlertDTO> triggered = enabledAlerts.stream()
                .filter(alert -> isAlertTriggered(alert, value))
                .map(glucoseAlertMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Threshold check for pet: {}, value: {}, triggered: {} alerts",
                petId, glucoseValue, triggered.size());
        return triggered;
    }

    /**
     * Updates an existing glucose alert with the provided fields (partial update supported).
     *
     * @param alertId the glucose alert identifier
     * @param userId  the authenticated user identifier
     * @param request the update request (only non-null fields are applied)
     * @return the updated {@link GlucoseAlertDTO}
     * @throws ResourceNotFoundException if the alert is not found or user is unauthorized
     */
    public GlucoseAlertDTO updateGlucoseAlert(Long alertId, Long userId, UpdateGlucoseAlertRequest request) {
        GlucoseAlert alert = glucoseAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose alert not found with id: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            log.warn("Unauthorized update to glucose alert: {} by user: {}", alertId, userId);
            throw new ResourceNotFoundException("Glucose alert not found or unauthorized access");
        }

        if (request.getAlertType() != null) {
            alert.setAlertType(request.getAlertType());
        }
        if (request.getLowThreshold() != null) {
            alert.setLowThreshold(request.getLowThreshold());
        }
        if (request.getHighThreshold() != null) {
            alert.setHighThreshold(request.getHighThreshold());
        }
        if (request.getIsEnabled() != null) {
            alert.setIsEnabled(request.getIsEnabled());
        }
        if (request.getDescription() != null) {
            alert.setDescription(request.getDescription());
        }

        alert = glucoseAlertRepository.save(alert);
        log.info("Glucose alert updated successfully: {} for user: {}", alertId, userId);
        return glucoseAlertMapper.toDTO(alert);
    }

    /**
     * Soft-deletes a glucose alert by setting its {@code isEnabled} flag to {@code false}.
     *
     * @param alertId the glucose alert identifier
     * @param userId  the authenticated user identifier
     * @throws ResourceNotFoundException if the alert is not found or user is unauthorized
     */
    public void deleteGlucoseAlert(Long alertId, Long userId) {
        GlucoseAlert alert = glucoseAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose alert not found with id: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            log.warn("Unauthorized delete of glucose alert: {} by user: {}", alertId, userId);
            throw new ResourceNotFoundException("Glucose alert not found or unauthorized access");
        }

        alert.setIsEnabled(false);
        glucoseAlertRepository.save(alert);
        log.info("Glucose alert deleted (soft delete) successfully: {} for user: {}", alertId, userId);
    }

    /**
     * Determines whether a given glucose value triggers the specified alert.
     *
     * @param alert the alert configuration
     * @param value the glucose reading value
     * @return {@code true} if the alert is triggered
     */
    private boolean isAlertTriggered(GlucoseAlert alert, BigDecimal value) {
        switch (alert.getAlertType()) {
            case LOW_GLUCOSE:
                return alert.getLowThreshold() != null && value.compareTo(alert.getLowThreshold()) < 0;
            case HIGH_GLUCOSE:
                return alert.getHighThreshold() != null && value.compareTo(alert.getHighThreshold()) > 0;
            case CRITICAL_GLUCOSE:
                boolean tooLow = alert.getLowThreshold() != null && value.compareTo(alert.getLowThreshold()) < 0;
                boolean tooHigh = alert.getHighThreshold() != null && value.compareTo(alert.getHighThreshold()) > 0;
                return tooLow || tooHigh;
            case MISSED_READING:
                // MISSED_READING requires time-based logic and cannot be triggered by a glucose value check.
                return false;
            default:
                return false;
        }
    }

}
