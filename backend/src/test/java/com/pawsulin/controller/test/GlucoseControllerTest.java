package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.GlucoseController;
import com.pawsulin.dto.CreateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseReadingDTO;
import com.pawsulin.dto.UpdateGlucoseReadingRequest;
import com.pawsulin.dto.GlucoseAnalyticsDTO;
import com.pawsulin.security.UserPrincipal;
import com.pawsulin.service.GlucoseService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlucoseController Tests")
class GlucoseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GlucoseService glucoseService;

    @InjectMocks
    private GlucoseController glucoseController;

    private ObjectMapper objectMapper;
    private GlucoseReadingDTO testReadingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(glucoseController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "test@example.com", "password", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PET_OWNER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testReadingDTO = GlucoseReadingDTO.builder()
                .id(1L)
                .petId(1L)
                .userId(1L)
                .glucoseValue(new BigDecimal("125.50"))
                .glucoseLevel("NORMAL")
                .readingTime(LocalDateTime.now())
                .notes("Morning reading")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create glucose reading successfully")
    void testCreateGlucoseReadingSuccess() throws Exception {
        CreateGlucoseReadingRequest request = CreateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("125.50"))
                .readingTime(LocalDateTime.now())
                .notes("Morning reading")
                .build();

        when(glucoseService.createGlucoseReading(anyLong(), anyLong(), any(CreateGlucoseReadingRequest.class)))
                .thenReturn(testReadingDTO);

        mockMvc.perform(post("/api/v1/pets/1/glucose")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.glucoseValue").value(125.50))
                .andExpect(jsonPath("$.glucoseLevel").value("NORMAL"));
    }

    @Test
    @DisplayName("Should get glucose reading by id successfully")
    void testGetGlucoseReadingByIdSuccess() throws Exception {
        when(glucoseService.getGlucoseReadingById(anyLong(), anyLong()))
                .thenReturn(testReadingDTO);

        mockMvc.perform(get("/api/v1/pets/1/glucose/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.glucoseLevel").value("NORMAL"));
    }

    @Test
    @DisplayName("Should get paginated glucose readings")
    void testGetGlucoseReadingsSuccess() throws Exception {
        Page<GlucoseReadingDTO> readingsPage = new PageImpl<>(List.of(testReadingDTO), PageRequest.of(0, 10), 1);
        when(glucoseService.getGlucoseReadingsByPetId(anyLong(), anyLong(), any()))
                .thenReturn(readingsPage);

        mockMvc.perform(get("/api/v1/pets/1/glucose")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should update glucose reading successfully")
    void testUpdateGlucoseReadingSuccess() throws Exception {
        UpdateGlucoseReadingRequest request = UpdateGlucoseReadingRequest.builder()
                .glucoseValue(new BigDecimal("150.00"))
                .build();

        when(glucoseService.updateGlucoseReading(anyLong(), anyLong(), any(UpdateGlucoseReadingRequest.class)))
                .thenReturn(testReadingDTO);

        mockMvc.perform(put("/api/v1/pets/1/glucose/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete glucose reading successfully")
    void testDeleteGlucoseReadingSuccess() throws Exception {
        doNothing().when(glucoseService).deleteGlucoseReading(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/pets/1/glucose/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get glucose analytics successfully")
    void testGetGlucoseAnalyticsSuccess() throws Exception {
        GlucoseAnalyticsDTO analytics = GlucoseAnalyticsDTO.builder()
                .averageGlucose(new BigDecimal("125.50"))
                .minGlucose(new BigDecimal("100.00"))
                .maxGlucose(new BigDecimal("150.00"))
                .readingsCount(10L)
                .normalReadingsCount(8L)
                .lowReadingsCount(1L)
                .highReadingsCount(1L)
                .criticalReadingsCount(0L)
                .build();

        when(glucoseService.getGlucoseAnalytics(anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(analytics);

        mockMvc.perform(get("/api/v1/pets/1/glucose/analytics")
                .param("startTime", "2026-07-10T00:00:00")
                .param("endTime", "2026-07-18T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readingsCount").value(10))
                .andExpect(jsonPath("$.averageGlucose").value(125.50));
    }
}
