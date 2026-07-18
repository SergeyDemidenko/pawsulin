package com.pawsulin.service;

import com.pawsulin.dto.CreateInsulinLogRequest;
import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.dto.UpdateInsulinLogRequest;
import com.pawsulin.entity.InsulinLog;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.InsulinLogMapper;
import com.pawsulin.repository.InsulinLogRepository;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing insulin log operations.
 * Implements full CRUD with pagination, date-range queries, and soft delete.
 */
@Service
@Slf4j
@Transactional
public class InsulinService {

    @Autowired
    private InsulinLogRepository insulinLogRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InsulinLogMapper insulinLogMapper;

    /**
     * Creates a new insulin log entry for a pet.
     *
     * @param petId   the pet identifier
     * @param userId  the authenticated user identifier
     * @param request the create request with insulin details
     * @return the created {@link InsulinLogDTO}
     * @throws ResourceNotFoundException if pet or user not found, or user is unauthorized
     */
    public InsulinLogDTO createInsulinLog(Long petId, Long userId, CreateInsulinLogRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized insulin log creation for pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        InsulinLog insulinLog = InsulinLog.builder()
                .pet(pet)
                .user(user)
                .insulinType(request.getInsulinType())
                .amountUnits(request.getAmountUnits())
                .injectionTime(request.getInjectionTime())
                .batchNumber(request.getBatchNumber())
                .expirationDate(request.getExpirationDate())
                .notes(request.getNotes())
                .isActive(true)
                .build();

        insulinLog = insulinLogRepository.save(insulinLog);
        log.info("Insulin log created successfully: {} for pet: {}", insulinLog.getId(), petId);
        return insulinLogMapper.toDTO(insulinLog);
    }

    /**
     * Retrieves a single insulin log by its identifier.
     *
     * @param logId  the insulin log identifier
     * @param userId the authenticated user identifier
     * @return the matching {@link InsulinLogDTO}
     * @throws ResourceNotFoundException if the log is not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public InsulinLogDTO getInsulinLogById(Long logId, Long userId) {
        InsulinLog insulinLog = insulinLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Insulin log not found with id: " + logId));

        if (!insulinLog.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to insulin log: {} by user: {}", logId, userId);
            throw new ResourceNotFoundException("Insulin log not found or unauthorized access");
        }

        return insulinLogMapper.toDTO(insulinLog);
    }

    /**
     * Returns a paginated list of active insulin logs for a pet.
     *
     * @param petId    the pet identifier
     * @param userId   the authenticated user identifier
     * @param pageable pagination parameters
     * @return paginated {@link InsulinLogDTO} results
     * @throws ResourceNotFoundException if pet not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public Page<InsulinLogDTO> getInsulinLogsByPetId(Long petId, Long userId, Pageable pageable) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        Page<InsulinLog> logs = insulinLogRepository.findByPetIdAndIsActiveTrue(petId, pageable);
        log.info("Retrieved {} insulin logs for pet: {}", logs.getTotalElements(), petId);
        return logs.map(insulinLogMapper::toDTO);
    }

    /**
     * Returns insulin logs for a pet within the specified date range.
     *
     * @param petId     the pet identifier
     * @param userId    the authenticated user identifier
     * @param startTime start of the date range (inclusive)
     * @param endTime   end of the date range (inclusive)
     * @return list of {@link InsulinLogDTO} within the range
     * @throws ResourceNotFoundException if pet not found or user is unauthorized
     */
    @Transactional(readOnly = true)
    public List<InsulinLogDTO> getInsulinLogsByDateRange(Long petId, Long userId,
                                                         LocalDateTime startTime, LocalDateTime endTime) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        List<InsulinLog> logs = insulinLogRepository.findByPetIdAndInjectionTimeBetween(petId, startTime, endTime);
        log.info("Retrieved {} insulin logs for pet: {} between {} and {}", logs.size(), petId, startTime, endTime);
        return logs.stream()
                .filter(InsulinLog::getIsActive)
                .map(insulinLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing insulin log with the provided fields (partial update supported).
     *
     * @param logId   the insulin log identifier
     * @param userId  the authenticated user identifier
     * @param request the update request (only non-null fields are applied)
     * @return the updated {@link InsulinLogDTO}
     * @throws ResourceNotFoundException if the log is not found or user is unauthorized
     */
    public InsulinLogDTO updateInsulinLog(Long logId, Long userId, UpdateInsulinLogRequest request) {
        InsulinLog insulinLog = insulinLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Insulin log not found with id: " + logId));

        if (!insulinLog.getUser().getId().equals(userId)) {
            log.warn("Unauthorized update to insulin log: {} by user: {}", logId, userId);
            throw new ResourceNotFoundException("Insulin log not found or unauthorized access");
        }

        if (request.getInsulinType() != null) {
            insulinLog.setInsulinType(request.getInsulinType());
        }
        if (request.getAmountUnits() != null) {
            insulinLog.setAmountUnits(request.getAmountUnits());
        }
        if (request.getInjectionTime() != null) {
            insulinLog.setInjectionTime(request.getInjectionTime());
        }
        if (request.getBatchNumber() != null) {
            insulinLog.setBatchNumber(request.getBatchNumber());
        }
        if (request.getExpirationDate() != null) {
            insulinLog.setExpirationDate(request.getExpirationDate());
        }
        if (request.getNotes() != null) {
            insulinLog.setNotes(request.getNotes());
        }

        insulinLog = insulinLogRepository.save(insulinLog);
        log.info("Insulin log updated successfully: {} for user: {}", logId, userId);
        return insulinLogMapper.toDTO(insulinLog);
    }

    /**
     * Soft-deletes an insulin log by setting its {@code isActive} flag to {@code false}.
     *
     * @param logId  the insulin log identifier
     * @param userId the authenticated user identifier
     * @throws ResourceNotFoundException if the log is not found or user is unauthorized
     */
    public void deleteInsulinLog(Long logId, Long userId) {
        InsulinLog insulinLog = insulinLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Insulin log not found with id: " + logId));

        if (!insulinLog.getUser().getId().equals(userId)) {
            log.warn("Unauthorized delete of insulin log: {} by user: {}", logId, userId);
            throw new ResourceNotFoundException("Insulin log not found or unauthorized access");
        }

        insulinLog.setIsActive(false);
        insulinLogRepository.save(insulinLog);
        log.info("Insulin log deleted (soft delete) successfully: {} for user: {}", logId, userId);
    }

}
