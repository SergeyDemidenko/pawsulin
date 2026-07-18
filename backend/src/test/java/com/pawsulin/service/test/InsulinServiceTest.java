package com.pawsulin.service.test;

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
import com.pawsulin.service.InsulinService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InsulinService Tests")
class InsulinServiceTest {

    @Mock
    private InsulinLogRepository insulinLogRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InsulinLogMapper insulinLogMapper;

    @InjectMocks
    private InsulinService insulinService;

    private User testUser;
    private Pet testPet;
    private InsulinLog testLog;
    private CreateInsulinLogRequest createRequest;

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

        testLog = InsulinLog.builder()
                .id(1L)
                .pet(testPet)
                .user(testUser)
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.now())
                .batchNumber("BATCH001")
                .expirationDate(LocalDate.now().plusMonths(6))
                .notes("Morning dose")
                .isActive(true)
                .build();

        createRequest = CreateInsulinLogRequest.builder()
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.now())
                .batchNumber("BATCH001")
                .expirationDate(LocalDate.now().plusMonths(6))
                .notes("Morning dose")
                .build();

        lenient().when(insulinLogMapper.toDTO(any(InsulinLog.class)))
                .thenAnswer(invocation -> {
                    InsulinLog log = invocation.getArgument(0);
                    return InsulinLogDTO.builder()
                            .id(log.getId())
                            .petId(log.getPet() != null ? log.getPet().getId() : null)
                            .userId(log.getUser() != null ? log.getUser().getId() : null)
                            .insulinType(log.getInsulinType())
                            .amountUnits(log.getAmountUnits())
                            .injectionTime(log.getInjectionTime())
                            .batchNumber(log.getBatchNumber())
                            .expirationDate(log.getExpirationDate())
                            .notes(log.getNotes())
                            .createdAt(log.getCreatedAt())
                            .updatedAt(log.getUpdatedAt())
                            .build();
                });
    }

    @Test
    @DisplayName("Should create insulin log successfully")
    void testCreateInsulinLogSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(insulinLogRepository.save(any(InsulinLog.class))).thenReturn(testLog);

        InsulinLogDTO result = insulinService.createInsulinLog(1L, 1L, createRequest);

        assertNotNull(result);
        assertEquals("Lantus", result.getInsulinType());
        assertEquals(new BigDecimal("5.0"), result.getAmountUnits());
        verify(insulinLogRepository, times(1)).save(any(InsulinLog.class));
    }

    @Test
    @DisplayName("Should throw exception when pet not found during insulin log creation")
    void testCreateInsulinLogPetNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.createInsulinLog(1L, 1L, createRequest));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user creates insulin log")
    void testCreateInsulinLogUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.createInsulinLog(1L, 999L, createRequest));
    }

    @Test
    @DisplayName("Should get insulin log by id successfully")
    void testGetInsulinLogByIdSuccess() {
        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        InsulinLogDTO result = insulinService.getInsulinLogById(1L, 1L);

        assertNotNull(result);
        assertEquals("Lantus", result.getInsulinType());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw exception when insulin log not found")
    void testGetInsulinLogByIdNotFound() {
        when(insulinLogRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.getInsulinLogById(1L, 1L));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user accesses insulin log")
    void testGetInsulinLogByIdUnauthorized() {
        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.getInsulinLogById(1L, 999L));
    }

    @Test
    @DisplayName("Should get paginated insulin logs by pet id successfully")
    void testGetInsulinLogsByPetIdSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        Page<InsulinLog> logsPage = new PageImpl<>(List.of(testLog), PageRequest.of(0, 10), 1);
        when(insulinLogRepository.findByPetIdAndIsActiveTrue(1L, PageRequest.of(0, 10)))
                .thenReturn(logsPage);

        Page<InsulinLogDTO> result = insulinService.getInsulinLogsByPetId(1L, 1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Lantus", result.getContent().get(0).getInsulinType());
    }

    @Test
    @DisplayName("Should update insulin log successfully")
    void testUpdateInsulinLogSuccess() {
        UpdateInsulinLogRequest updateRequest = UpdateInsulinLogRequest.builder()
                .amountUnits(new BigDecimal("8.0"))
                .notes("Updated dose")
                .build();

        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));
        when(insulinLogRepository.save(any(InsulinLog.class))).thenReturn(testLog);

        InsulinLogDTO result = insulinService.updateInsulinLog(1L, 1L, updateRequest);

        assertNotNull(result);
        verify(insulinLogRepository, times(1)).save(any(InsulinLog.class));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user updates insulin log")
    void testUpdateInsulinLogUnauthorized() {
        UpdateInsulinLogRequest updateRequest = UpdateInsulinLogRequest.builder()
                .amountUnits(new BigDecimal("8.0"))
                .build();

        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.updateInsulinLog(1L, 999L, updateRequest));
    }

    @Test
    @DisplayName("Should delete insulin log successfully (soft delete)")
    void testDeleteInsulinLogSuccess() {
        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));
        when(insulinLogRepository.save(any(InsulinLog.class))).thenReturn(testLog);

        insulinService.deleteInsulinLog(1L, 1L);

        verify(insulinLogRepository, times(1)).save(any(InsulinLog.class));
        assertFalse(testLog.getIsActive());
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user deletes insulin log")
    void testDeleteInsulinLogUnauthorized() {
        when(insulinLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        assertThrows(ResourceNotFoundException.class,
                () -> insulinService.deleteInsulinLog(1L, 999L));
    }
}
