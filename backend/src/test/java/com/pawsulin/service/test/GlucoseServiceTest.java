package com.pawsulin.service.test;

import com.pawsulin.dto.CreateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.UpdateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseAnalyticsDTO;
import com.pawsulin.entity.GlucoseReading;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.GlucoseReadingMapper;
import com.pawsulin.repository.GlucoseReadingRepository;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import com.pawsulin.service.GlucoseService;
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
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlucoseService Tests")
class GlucoseServiceTest {

    @Mock
    private GlucoseReadingRepository glucoseReadingRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GlucoseReadingMapper glucoseReadingMapper;

    @InjectMocks
    private GlucoseService glucoseService;

    private User testUser;
    private Pet testPet;
    private GlucoseReading testReading;
    private CreateGlucoseReadingRequest createRequest;

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

        testReading = GlucoseReading.builder()
                .id(1L)
                .pet(testPet)
                .user(testUser)
                .glucoseValue(new BigDecimal("125.50"))
                .glucoseLevel(GlucoseReading.GlucoseLevel.NORMAL)
                .readingTime(LocalDateTime.now())
                .notes("Morning reading")
                .isActive(true)
                .build();

        createRequest = CreateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("125.50"))
                .readingTime(LocalDateTime.now())
                .notes("Morning reading")
                .build();

        lenient().when(glucoseReadingMapper.toDTO(any(GlucoseReading.class)))
                .thenAnswer(invocation -> {
                    GlucoseReading reading = invocation.getArgument(0);
                    return GlucoseReadingDTO.builder()
                            .id(reading.getId())
                            .petId(reading.getPet() != null ? reading.getPet().getId() : null)
                            .userId(reading.getUser() != null ? reading.getUser().getId() : null)
                            .glucoseValue(reading.getGlucoseValue())
                            .glucoseLevel(reading.getGlucoseLevel() != null ? reading.getGlucoseLevel().name() : null)
                            .readingTime(reading.getReadingTime())
                            .notes(reading.getNotes())
                            .createdAt(reading.getCreatedAt())
                            .updatedAt(reading.getUpdatedAt())
                            .build();
                });
    }

    @Test
    @DisplayName("Should create glucose reading successfully")
    void testCreateGlucoseReadingSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(glucoseReadingRepository.save(any(GlucoseReading.class))).thenReturn(testReading);

        GlucoseReadingDTO result = glucoseService.createGlucoseReading(1L, 1L, createRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("125.50"), result.getGlucoseValue());
        verify(glucoseReadingRepository, times(1)).save(any(GlucoseReading.class));
    }

    @Test
    @DisplayName("Should throw exception when pet not found during glucose creation")
    void testCreateGlucoseReadingPetNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glucoseService.createGlucoseReading(1L, 1L, createRequest));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user creates glucose reading")
    void testCreateGlucoseReadingUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class, () -> glucoseService.createGlucoseReading(1L, 999L, createRequest));
    }

    @Test
    @DisplayName("Should get glucose reading by id successfully")
    void testGetGlucoseReadingByIdSuccess() {
        when(glucoseReadingRepository.findById(1L)).thenReturn(Optional.of(testReading));

        GlucoseReadingDTO result = glucoseService.getGlucoseReadingById(1L, 1L);

        assertNotNull(result);
        assertEquals(new BigDecimal("125.50"), result.getGlucoseValue());
    }

    @Test
    @DisplayName("Should throw exception when glucose reading not found")
    void testGetGlucoseReadingByIdNotFound() {
        when(glucoseReadingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glucoseService.getGlucoseReadingById(1L, 1L));
    }

    @Test
    @DisplayName("Should get paginated glucose readings by pet id")
    void testGetGlucoseReadingsByPetIdSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        Page<GlucoseReading> readingsPage = new PageImpl<>(List.of(testReading), PageRequest.of(0, 10), 1);
        when(glucoseReadingRepository.findByPetIdAndIsActiveTrue(1L, PageRequest.of(0, 10)))
                .thenReturn(readingsPage);

        Page<GlucoseReadingDTO> result = glucoseService.getGlucoseReadingsByPetId(1L, 1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should get glucose readings by date range")
    void testGetGlucoseReadingsByDateRange() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseReadingRepository.findByPetIdAndReadingTimeBetween(any(), any(), any()))
                .thenReturn(List.of(testReading));

        List<GlucoseReadingDTO> result = glucoseService.getGlucoseReadingsByDateRange(
                1L, 1L, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should update glucose reading successfully")
    void testUpdateGlucoseReadingSuccess() {
        UpdateGlucoseReadingRequest updateRequest = UpdateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("150.00"))
                .build();

        when(glucoseReadingRepository.findById(1L)).thenReturn(Optional.of(testReading));
        when(glucoseReadingRepository.save(any(GlucoseReading.class))).thenReturn(testReading);

        GlucoseReadingDTO result = glucoseService.updateGlucoseReading(1L, 1L, updateRequest);

        assertNotNull(result);
        verify(glucoseReadingRepository, times(1)).save(any(GlucoseReading.class));
    }

    @Test
    @DisplayName("Should delete glucose reading successfully")
    void testDeleteGlucoseReadingSuccess() {
        when(glucoseReadingRepository.findById(1L)).thenReturn(Optional.of(testReading));
        when(glucoseReadingRepository.save(any(GlucoseReading.class))).thenReturn(testReading);

        glucoseService.deleteGlucoseReading(1L, 1L);

        verify(glucoseReadingRepository, times(1)).save(any(GlucoseReading.class));
    }

    @Test
    @DisplayName("Should calculate glucose analytics successfully")
    void testGetGlucoseAnalyticsSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(glucoseReadingRepository.findByPetIdAndReadingTimeBetween(any(), any(), any()))
                .thenReturn(List.of(testReading));

        GlucoseAnalyticsDTO result = glucoseService.getGlucoseAnalytics(
                1L, 1L, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1L, result.getReadingsCount());
        assertNotNull(result.getAverageGlucose());
    }

    @Test
    @DisplayName("Should determine glucose level as LOW")
    void testDetermineLowGlucoseLevel() {
        GlucoseReading lowReading = GlucoseReading.builder()
                .glucoseValue(new BigDecimal("65"))
                .glucoseLevel(GlucoseReading.GlucoseLevel.LOW)
                .pet(testPet)
                .user(testUser)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(glucoseReadingRepository.save(any(GlucoseReading.class))).thenReturn(lowReading);

        CreateGlucoseReadingRequest lowRequest = CreateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("65"))
                .readingTime(LocalDateTime.now())
                .build();

        GlucoseReadingDTO result = glucoseService.createGlucoseReading(1L, 1L, lowRequest);

        assertNotNull(result);
        assertEquals("LOW", result.getGlucoseLevel());
    }
}
