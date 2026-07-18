package com.pawsulin.service;

import com.pawsulin.dto.CreateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseAlertDTO;
import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.NotificationMessage;
import com.pawsulin.dto.UpdateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseAnalyticsDTO;
import com.pawsulin.entity.GlucoseReading;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.GlucoseReadingMapper;
import com.pawsulin.repository.GlucoseAlertRepository;
import com.pawsulin.repository.GlucoseReadingRepository;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class GlucoseService {

    @Autowired
    private GlucoseReadingRepository glucoseReadingRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlucoseReadingMapper glucoseReadingMapper;

    @Autowired
    private GlucoseAlertService glucoseAlertService;

    @Autowired
    private NotificationService notificationService;

    public GlucoseReadingDTO createGlucoseReading(Long petId, Long userId, CreateGlucoseReadingRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized glucose reading creation for pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        GlucoseReading.GlucoseLevel glucoseLevel = determineGlucoseLevel(request.getGlucoseValue());

        GlucoseReading reading = GlucoseReading.builder()
                .pet(pet)
                .user(user)
                .glucoseValue(request.getGlucoseValue())
                .glucoseLevel(glucoseLevel)
                .readingTime(request.getReadingTime())
                .notes(request.getNotes())
                .isActive(true)
                .build();

        reading = glucoseReadingRepository.save(reading);
        log.info("Glucose reading created successfully: {} for pet: {}", reading.getId(), petId);

        checkAndSendAlertNotifications(petId, userId, pet.getName(), request.getGlucoseValue());

        return glucoseReadingMapper.toDTO(reading);
    }

    @Transactional(readOnly = true)
    public GlucoseReadingDTO getGlucoseReadingById(Long readingId, Long userId) {
        GlucoseReading reading = glucoseReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose reading not found with id: " + readingId));

        if (!reading.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to glucose reading: {} by user: {}", readingId, userId);
            throw new ResourceNotFoundException("Glucose reading not found or unauthorized access");
        }

        return glucoseReadingMapper.toDTO(reading);
    }

    @Transactional(readOnly = true)
    public Page<GlucoseReadingDTO> getGlucoseReadingsByPetId(Long petId, Long userId, Pageable pageable) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        Page<GlucoseReading> readings = glucoseReadingRepository.findByPetIdAndIsActiveTrue(petId, pageable);
        log.info("Retrieved {} glucose readings for pet: {}", readings.getTotalElements(), petId);
        return readings.map(glucoseReadingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<GlucoseReadingDTO> getGlucoseReadingsByDateRange(Long petId, Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        List<GlucoseReading> readings = glucoseReadingRepository.findByPetIdAndReadingTimeBetween(petId, startTime, endTime);
        log.info("Retrieved {} glucose readings for pet: {} between {} and {}", readings.size(), petId, startTime, endTime);
        return readings.stream()
                .filter(GlucoseReading::getIsActive)
                .map(glucoseReadingMapper::toDTO)
                .collect(Collectors.toList());
    }

    public GlucoseReadingDTO updateGlucoseReading(Long readingId, Long userId, UpdateGlucoseReadingRequest request) {
        GlucoseReading reading = glucoseReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose reading not found with id: " + readingId));

        if (!reading.getUser().getId().equals(userId)) {
            log.warn("Unauthorized update to glucose reading: {} by user: {}", readingId, userId);
            throw new ResourceNotFoundException("Glucose reading not found or unauthorized access");
        }

        if (request.getGlucoseValue() != null) {
            reading.setGlucoseValue(request.getGlucoseValue());
            reading.setGlucoseLevel(determineGlucoseLevel(request.getGlucoseValue()));
        }
        if (request.getNotes() != null) {
            reading.setNotes(request.getNotes());
        }

        reading = glucoseReadingRepository.save(reading);
        log.info("Glucose reading updated successfully: {} for user: {}", readingId, userId);
        return glucoseReadingMapper.toDTO(reading);
    }

    public void deleteGlucoseReading(Long readingId, Long userId) {
        GlucoseReading reading = glucoseReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Glucose reading not found with id: " + readingId));

        if (!reading.getUser().getId().equals(userId)) {
            log.warn("Unauthorized delete of glucose reading: {} by user: {}", readingId, userId);
            throw new ResourceNotFoundException("Glucose reading not found or unauthorized access");
        }

        reading.setIsActive(false);
        glucoseReadingRepository.save(reading);
        log.info("Glucose reading deleted (soft delete) successfully: {} for user: {}", readingId, userId);
    }

    @Transactional(readOnly = true)
    public GlucoseAnalyticsDTO getGlucoseAnalytics(Long petId, Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access to pet analytics: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        List<GlucoseReading> readings = glucoseReadingRepository.findByPetIdAndReadingTimeBetween(petId, startTime, endTime);
        readings = readings.stream().filter(GlucoseReading::getIsActive).collect(Collectors.toList());

        if (readings.isEmpty()) {
            log.info("No glucose readings found for pet: {} in the specified date range", petId);
            return new GlucoseAnalyticsDTO();
        }

        return calculateAnalytics(readings);
    }

    private GlucoseAnalyticsDTO calculateAnalytics(List<GlucoseReading> readings) {
        BigDecimal sum = readings.stream()
                .map(GlucoseReading::getGlucoseValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = sum.divide(BigDecimal.valueOf(readings.size()), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal min = readings.stream()
                .map(GlucoseReading::getGlucoseValue)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal max = readings.stream()
                .map(GlucoseReading::getGlucoseValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        long lowCount = readings.stream()
                .filter(r -> r.getGlucoseLevel() == GlucoseReading.GlucoseLevel.LOW)
                .count();
        long normalCount = readings.stream()
                .filter(r -> r.getGlucoseLevel() == GlucoseReading.GlucoseLevel.NORMAL)
                .count();
        long highCount = readings.stream()
                .filter(r -> r.getGlucoseLevel() == GlucoseReading.GlucoseLevel.HIGH)
                .count();
        long criticalCount = readings.stream()
                .filter(r -> r.getGlucoseLevel() == GlucoseReading.GlucoseLevel.CRITICAL)
                .count();

        long total = readings.size();
        BigDecimal lowPercentage = BigDecimal.valueOf(lowCount).divide(BigDecimal.valueOf(total), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal normalPercentage = BigDecimal.valueOf(normalCount).divide(BigDecimal.valueOf(total), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal highPercentage = BigDecimal.valueOf(highCount).divide(BigDecimal.valueOf(total), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal criticalPercentage = BigDecimal.valueOf(criticalCount).divide(BigDecimal.valueOf(total), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));

        return GlucoseAnalyticsDTO.builder()
                .averageGlucose(average)
                .minGlucose(min)
                .maxGlucose(max)
                .readingsCount(total)
                .lowReadingsCount(lowCount)
                .normalReadingsCount(normalCount)
                .highReadingsCount(highCount)
                .criticalReadingsCount(criticalCount)
                .lowPercentage(lowPercentage)
                .normalPercentage(normalPercentage)
                .highPercentage(highPercentage)
                .criticalPercentage(criticalPercentage)
                .build();
    }

    /**
     * Checks configured alert thresholds for the new glucose value and sends WebSocket
     * notifications for any that are triggered.
     */
    private void checkAndSendAlertNotifications(Long petId, Long userId, String petName, BigDecimal glucoseValue) {
        try {
            List<GlucoseAlertDTO> triggered = glucoseAlertService.checkAlertThresholds(petId, userId, glucoseValue.intValue());
            for (GlucoseAlertDTO alert : triggered) {
                String description = alert.getDescription() != null ? alert.getDescription()
                        : buildDefaultAlertMessage(alert.getAlertType(), glucoseValue, petName);
                NotificationMessage notification = NotificationMessage.builder()
                        .type("GLUCOSE_ALERT")
                        .petId(petId)
                        .petName(petName)
                        .alertType(alert.getAlertType())
                        .glucoseValue(glucoseValue)
                        .message(description)
                        .triggeredAt(LocalDateTime.now())
                        .build();
                notificationService.sendNotification(userId, notification);
            }
        } catch (Exception e) {
            log.warn("Alert threshold check failed for pet: {}; notifications skipped", petId, e);
        }
    }

    private String buildDefaultAlertMessage(com.pawsulin.entity.GlucoseAlert.AlertType alertType, BigDecimal glucoseValue, String petName) {
        return switch (alertType) {
            case LOW_GLUCOSE -> String.format("%s has a low glucose reading: %.1f mg/dL", petName, glucoseValue);
            case HIGH_GLUCOSE -> String.format("%s has a high glucose reading: %.1f mg/dL", petName, glucoseValue);
            case CRITICAL_GLUCOSE -> String.format("%s has a critical glucose reading: %.1f mg/dL", petName, glucoseValue);
            case MISSED_READING -> String.format("Missed glucose reading detected for %s", petName);
        };
    }

    private GlucoseReading.GlucoseLevel determineGlucoseLevel(BigDecimal glucoseValue) {
        if (glucoseValue.compareTo(BigDecimal.valueOf(70)) < 0) {
            return GlucoseReading.GlucoseLevel.LOW;
        } else if (glucoseValue.compareTo(BigDecimal.valueOf(70)) >= 0 && glucoseValue.compareTo(BigDecimal.valueOf(180)) <= 0) {
            return GlucoseReading.GlucoseLevel.NORMAL;
        } else if (glucoseValue.compareTo(BigDecimal.valueOf(180)) > 0 && glucoseValue.compareTo(BigDecimal.valueOf(300)) <= 0) {
            return GlucoseReading.GlucoseLevel.HIGH;
        } else {
            return GlucoseReading.GlucoseLevel.CRITICAL;
        }
    }

}
