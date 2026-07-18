package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.PetController;
import com.pawsulin.dto.CreatePetRequest;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UpdatePetRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetController Tests")
class PetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private ObjectMapper objectMapper;
    private PetDTO testPetDTO;
    private CreatePetRequest createRequest;
    private UpdatePetRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
        objectMapper = new ObjectMapper();

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
    }

    @Test
    @DisplayName("Should create pet successfully")
    void testCreatePetSuccess() throws Exception {
        when(petService.createPet(any(Long.class), any(CreatePetRequest.class)))
                .thenReturn(testPetDTO);

        mockMvc.perform(post("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fluffy"))
                .andExpect(jsonPath("$.species").value("Cat"));
    }

    @Test
    @DisplayName("Should get pet by id successfully")
    void testGetPetByIdSuccess() throws Exception {
        when(petService.getPetById(eq(1L), any(Long.class)))
                .thenReturn(testPetDTO);

        mockMvc.perform(get("/api/v1/pets/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fluffy"));
    }

    @Test
    @DisplayName("Should get all pets with pagination")
    void testGetAllPetsSuccess() throws Exception {
        Page<PetDTO> petsPage = new PageImpl<>(List.of(testPetDTO), PageRequest.of(0, 10), 1);
        when(petService.getPetsByUserId(any(Long.class), any()))
                .thenReturn(petsPage);

        mockMvc.perform(get("/api/v1/pets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Fluffy"));
    }

    @Test
    @DisplayName("Should get all pets non-paginated")
    void testGetAllPetsNonPaginatedSuccess() throws Exception {
        when(petService.getAllPetsByUserId(any(Long.class)))
                .thenReturn(List.of(testPetDTO));

        mockMvc.perform(get("/api/v1/pets/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fluffy"));
    }

    @Test
    @DisplayName("Should update pet successfully")
    void testUpdatePetSuccess() throws Exception {
        when(petService.updatePet(eq(1L), any(Long.class), any(UpdatePetRequest.class)))
                .thenReturn(testPetDTO);

        mockMvc.perform(put("/api/v1/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fluffy"));
    }

    @Test
    @DisplayName("Should delete pet successfully")
    void testDeletePetSuccess() throws Exception {
        doNothing().when(petService).deletePet(eq(1L), any(Long.class));

        mockMvc.perform(delete("/api/v1/pets/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
