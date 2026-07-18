package com.pawsulin.service.test;

import com.pawsulin.dto.CreatePetRequest;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UpdatePetRequest;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.mapper.PetMapper;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import com.pawsulin.service.PetService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Tests")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetService petService;

    private User testUser;
    private Pet testPet;
    private CreatePetRequest createRequest;
    private UpdatePetRequest updateRequest;
    private PetDTO testPetDTO;

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
                .medicalNotes("Requires insulin twice daily")
                .isActive(true)
                .build();

        createRequest = CreatePetRequest.builder()
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .medicalNotes("Requires insulin twice daily")
                .build();

        updateRequest = UpdatePetRequest.builder()
                .name("Fluffy Updated")
                .ageYears(4)
                .build();

        testPetDTO = PetDTO.builder()
                .id(1L)
                .userId(1L)
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .medicalNotes("Requires insulin twice daily")
                .isActive(true)
                .build();

        lenient().when(petMapper.toDTO(any(Pet.class))).thenReturn(testPetDTO);
    }

    @Test
    @DisplayName("Should create pet successfully")
    void testCreatePetSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        PetDTO result = petService.createPet(1L, createRequest);

        assertNotNull(result);
        assertEquals("Fluffy", result.getName());
        assertEquals("Cat", result.getSpecies());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during pet creation")
    void testCreatePetUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.createPet(1L, createRequest));
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should get pet by id successfully")
    void testGetPetByIdSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        PetDTO result = petService.getPetById(1L, 1L);

        assertNotNull(result);
        assertEquals("Fluffy", result.getName());
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when pet not found")
    void testGetPetByIdNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.getPetById(1L, 1L));
    }

    @Test
    @DisplayName("Should throw exception when unauthorized access to pet")
    void testGetPetByIdUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class, () -> petService.getPetById(1L, 999L));
    }

    @Test
    @DisplayName("Should get paginated pets by user id")
    void testGetPetsByUserIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Page<Pet> petsPage = new PageImpl<>(List.of(testPet), PageRequest.of(0, 10), 1);
        when(petRepository.findByUserIdAndIsActiveTrue(1L, PageRequest.of(0, 10)))
                .thenReturn(petsPage);

        Page<PetDTO> result = petService.getPetsByUserId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(petRepository, times(1)).findByUserIdAndIsActiveTrue(1L, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should get all non-paginated pets by user id")
    void testGetAllPetsByUserIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByUserId(1L)).thenReturn(List.of(testPet));

        List<PetDTO> result = petService.getAllPetsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should update pet successfully")
    void testUpdatePetSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        PetDTO result = petService.updatePet(1L, 1L, updateRequest);

        assertNotNull(result);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should throw exception when updating unauthorized pet")
    void testUpdatePetUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class, () -> petService.updatePet(1L, 999L, updateRequest));
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should delete pet successfully (soft delete)")
    void testDeletePetSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        petService.deletePet(1L, 1L);

        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting unauthorized pet")
    void testDeletePetUnauthorized() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        assertThrows(ResourceNotFoundException.class, () -> petService.deletePet(1L, 999L));
        verify(petRepository, never()).save(any(Pet.class));
    }
}
