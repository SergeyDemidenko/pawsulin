package com.pawsulin.service.test;

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
import com.pawsulin.service.GlucoseAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlucoseAlertService Tests")
class GlucoseAlertServiceTest {

    @Mock
    private GlucoseAlertRepository glucoseAlertRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GlucoseAlertMapper glucoseAlertMapper;

    @InjectMocks
    private GlucoseAlertService glucoseAlertService;

    private User testUser;
    private Pet testPet;
    private GlucoseAlert testAlert;
    private CreateGlucoseAlertRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        testPet = Pet.builder()
                .id(1L)
                .user(testUser)
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .isActive(true)
                .build();

        testAlert = GlucoseAlert.builder()
                .id(1L)
                .pet(testPet)
                .user(testUser)
                .alertType(GlucoseAlert.AlertType.HIGH_GLUCOSE)
                .lowThreshold(new BigDecimal("60.0"))
                .highThreshold(new BigDecimal("250.0"))
                .isEnabled(true)
                .description("High glucose alert")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateGlucoseAlertRequest.builder()
                .alertType(GlucoseAlert.AlertType.HIGH_GLUCOSE)
                .lowThreshold(new BigDecimal("60.0"))
                .highThreshold(new BigDecimal("250.0"))
                .isEnabled(true)
                .description("High glucose alert")
                .build();

        lenient().when(glucoseAlertMapper.toDTO(any(GlucoseAlert.class)))
                .thenAnswer(invocation -> {
                    GlucoseAlert alert = invocation.getArgument(0);
                    return GlucoseAlertDTO.builder()
                            .id(alert.getId())
                            .petId(alert.getPet() != null ? alert.getPet().getId() : null)
                            .userId(alert.getUser() != null ? alert.getUser().getId() : null)
                            .alertType(alert.getAlertType())
                            .lowThreshold(alert.getLowThreshold())
                            .highThreshold(alert.getHighThreshold())
                            .isEnabled(alert.getIsEnabled())
                            .description(alert.getDescription())
                            .createdAt(alert.getCreatedAt())
                            .updatedAt(alert.getUpdatedAt())
                            .build();
                });
    }

    @Test
    @DisplayName("Should create glucose alert successfully")
    void testCreateGlucoseAlertSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(glucoseAlertRepository.save(any(GlucoseAlert.class))).thenReturn(testAlert);

        GlucoseAlertDTO result = glucoseAlertService.createGlucoseAlert(1L, 1L, createRequest);

        assertNotNull(result);
        assertEquals(GlucoseAlert.AlertType.HIGH_GLUCOSE, result.getAlertType());
        assertEquals(new BigDecimal("250.0"), result.getHighThreshold());
        assertEquals("High glucose alert", result.getDescription());
        verify(glucoseAlertRepository, times(1)).save(any(GlucoseAlert.class));
    }

    @Test
    @DisplayName("Should throw exception when pet not found during alert creation")
    void testCreateGlucoseAlertPetNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.createGlucoseAlert(1L, 1L, createRequest));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user creates glucose alert")
    void testCreateGlucoseAlertUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.createGlucoseAlert(1L, 999L, createRequest));
    }

    @Test
    @DisplayName("Should get glucose alert by id successfully")
    void testGetGlucoseAlertByIdSuccess() {
        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        GlucoseAlertDTO result = glucoseAlertService.getGlucoseAlertById(1L, 1L);

        assertNotNull(result);
        assertEquals(GlucoseAlert.AlertType.HIGH_GLUCOSE, result.getAlertType());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw exception when glucose alert not found")
    void testGetGlucoseAlertByIdNotFound() {
        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.getGlucoseAlertById(1L, 1L));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user accesses glucose alert")
    void testGetGlucoseAlertByIdUnauthorized() {
        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.getGlucoseAlertById(1L, 999L));
    }

    @Test
    @DisplayName("Should get paginated glucose alerts by pet id successfully")
    void testGetAlertsByPetIdSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        Page<GlucoseAlert> alertsPage = new PageImpl<>(List.of(testAlert), PageRequest.of(0, 10), 1);
        when(glucoseAlertRepository.findByPetIdAndIsEnabledTrue(1L, PageRequest.of(0, 10)))
                .thenReturn(alertsPage);

        Page<GlucoseAlertDTO> result = glucoseAlertService.getAlertsByPetId(1L, 1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(GlucoseAlert.AlertType.HIGH_GLUCOSE, result.getContent().get(0).getAlertType());
    }

    @Test
    @DisplayName("Should get paginated glucose alerts by user id successfully")
    void testGetAlertsByUserIdSuccess() {
        Page<GlucoseAlert> alertsPage = new PageImpl<>(List.of(testAlert), PageRequest.of(0, 10), 1);
        when(glucoseAlertRepository.findByUserIdAndIsEnabledTrue(1L, PageRequest.of(0, 10)))
                .thenReturn(alertsPage);

        Page<GlucoseAlertDTO> result = glucoseAlertService.getAlertsByUserId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(GlucoseAlert.AlertType.HIGH_GLUCOSE, result.getContent().get(0).getAlertType());
    }

    @Test
    @DisplayName("Should trigger HIGH_GLUCOSE alert when value exceeds high threshold")
    void testCheckAlertThresholdsHighGlucoseTriggered() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseAlertRepository.findByPetIdAndIsEnabledTrue(1L)).thenReturn(List.of(testAlert));

        List<GlucoseAlertDTO> triggered = glucoseAlertService.checkAlertThresholds(1L, 1L, BigDecimal.valueOf(300));

        assertEquals(1, triggered.size());
        assertEquals(GlucoseAlert.AlertType.HIGH_GLUCOSE, triggered.get(0).getAlertType());
    }

    @Test
    @DisplayName("Should not trigger alert when value is within normal range")
    void testCheckAlertThresholdsNoTrigger() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseAlertRepository.findByPetIdAndIsEnabledTrue(1L)).thenReturn(List.of(testAlert));

        List<GlucoseAlertDTO> triggered = glucoseAlertService.checkAlertThresholds(1L, 1L, BigDecimal.valueOf(150));

        assertEquals(0, triggered.size());
    }

    @Test
    @DisplayName("Should trigger CRITICAL_GLUCOSE alert when value is out of critical range")
    void testCheckAlertThresholdsCriticalTriggered() {
        GlucoseAlert criticalAlert = GlucoseAlert.builder()
                .id(2L)
                .pet(testPet)
                .user(testUser)
                .alertType(GlucoseAlert.AlertType.CRITICAL_GLUCOSE)
                .lowThreshold(new BigDecimal("40.0"))
                .highThreshold(new BigDecimal("400.0"))
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseAlertRepository.findByPetIdAndIsEnabledTrue(1L)).thenReturn(List.of(criticalAlert));

        List<GlucoseAlertDTO> triggered = glucoseAlertService.checkAlertThresholds(1L, 1L, BigDecimal.valueOf(30));

        assertEquals(1, triggered.size());
        assertEquals(GlucoseAlert.AlertType.CRITICAL_GLUCOSE, triggered.get(0).getAlertType());
    }

    @Test
    @DisplayName("Should trigger LOW_GLUCOSE alert when value is below low threshold")
    void testCheckAlertThresholdsLowGlucoseTriggered() {
        GlucoseAlert lowAlert = GlucoseAlert.builder()
                .id(3L)
                .pet(testPet)
                .user(testUser)
                .alertType(GlucoseAlert.AlertType.LOW_GLUCOSE)
                .lowThreshold(new BigDecimal("70.0"))
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseAlertRepository.findByPetIdAndIsEnabledTrue(1L)).thenReturn(List.of(lowAlert));

        List<GlucoseAlertDTO> triggered = glucoseAlertService.checkAlertThresholds(1L, 1L, BigDecimal.valueOf(50));

        assertEquals(1, triggered.size());
        assertEquals(GlucoseAlert.AlertType.LOW_GLUCOSE, triggered.get(0).getAlertType());
    }

    @Test
    @DisplayName("Should update glucose alert successfully")
    void testUpdateGlucoseAlertSuccess() {
        UpdateGlucoseAlertRequest updateRequest = UpdateGlucoseAlertRequest.builder()
                .highThreshold(new BigDecimal("300.0"))
                .description("Updated description")
                .build();

        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(glucoseAlertRepository.save(any(GlucoseAlert.class))).thenReturn(testAlert);

        GlucoseAlertDTO result = glucoseAlertService.updateGlucoseAlert(1L, 1L, updateRequest);

        assertNotNull(result);
        verify(glucoseAlertRepository, times(1)).save(any(GlucoseAlert.class));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user updates glucose alert")
    void testUpdateGlucoseAlertUnauthorized() {
        UpdateGlucoseAlertRequest updateRequest = UpdateGlucoseAlertRequest.builder()
                .highThreshold(new BigDecimal("300.0"))
                .build();

        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.updateGlucoseAlert(1L, 999L, updateRequest));
    }

    @Test
    @DisplayName("Should delete glucose alert successfully (soft delete)")
    void testDeleteGlucoseAlertSuccess() {
        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(glucoseAlertRepository.save(any(GlucoseAlert.class))).thenReturn(testAlert);

        glucoseAlertService.deleteGlucoseAlert(1L, 1L);

        verify(glucoseAlertRepository, times(1)).save(any(GlucoseAlert.class));
        assertFalse(testAlert.getIsEnabled());
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user deletes glucose alert")
    void testDeleteGlucoseAlertUnauthorized() {
        when(glucoseAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        assertThrows(ResourceNotFoundException.class,
                () -> glucoseAlertService.deleteGlucoseAlert(1L, 999L));
    }
}
