package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.GlucoseAlertController;
import com.pawsulin.dto.CreateGlucoseAlertRequest;
import com.pawsulin.dto.GlucoseAlertDTO;
import com.pawsulin.dto.UpdateGlucoseAlertRequest;
import com.pawsulin.entity.GlucoseAlert;
import com.pawsulin.security.UserPrincipal;
import com.pawsulin.service.GlucoseAlertService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlucoseAlertController Tests")
class GlucoseAlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GlucoseAlertService glucoseAlertService;

    @InjectMocks
    private GlucoseAlertController glucoseAlertController;

    private ObjectMapper objectMapper;
    private GlucoseAlertDTO testAlertDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(glucoseAlertController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "test@example.com", "password", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PET_OWNER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testAlertDTO = GlucoseAlertDTO.builder()
                .id(1L)
                .petId(1L)
                .userId(1L)
                .alertType(GlucoseAlert.AlertType.HIGH_GLUCOSE)
                .lowThreshold(new BigDecimal("60.0"))
                .highThreshold(new BigDecimal("250.0"))
                .isEnabled(true)
                .description("High glucose alert")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create glucose alert successfully")
    void testCreateGlucoseAlertSuccess() throws Exception {
        CreateGlucoseAlertRequest request = CreateGlucoseAlertRequest.builder()
                .alertType(GlucoseAlert.AlertType.HIGH_GLUCOSE)
                .highThreshold(new BigDecimal("250.0"))
                .build();

        when(glucoseAlertService.createGlucoseAlert(anyLong(), anyLong(), any(CreateGlucoseAlertRequest.class)))
                .thenReturn(testAlertDTO);

        mockMvc.perform(post("/api/v1/pets/1/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertType").value("HIGH_GLUCOSE"))
                .andExpect(jsonPath("$.highThreshold").value(250.0));
    }

    @Test
    @DisplayName("Should get glucose alert by id successfully")
    void testGetGlucoseAlertByIdSuccess() throws Exception {
        when(glucoseAlertService.getGlucoseAlertById(anyLong(), anyLong()))
                .thenReturn(testAlertDTO);

        mockMvc.perform(get("/api/v1/pets/1/alerts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alertType").value("HIGH_GLUCOSE"));
    }

    @Test
    @DisplayName("Should get paginated glucose alerts for pet successfully")
    void testGetAlertsByPetSuccess() throws Exception {
        Page<GlucoseAlertDTO> alertsPage = new PageImpl<>(List.of(testAlertDTO), PageRequest.of(0, 10), 1);
        when(glucoseAlertService.getAlertsByPetId(anyLong(), anyLong(), any()))
                .thenReturn(alertsPage);

        mockMvc.perform(get("/api/v1/pets/1/alerts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should get all user glucose alerts successfully")
    void testGetAlertsByUserSuccess() throws Exception {
        Page<GlucoseAlertDTO> alertsPage = new PageImpl<>(List.of(testAlertDTO), PageRequest.of(0, 10), 1);
        when(glucoseAlertService.getAlertsByUserId(anyLong(), any()))
                .thenReturn(alertsPage);

        mockMvc.perform(get("/api/v1/alerts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should update glucose alert successfully")
    void testUpdateGlucoseAlertSuccess() throws Exception {
        UpdateGlucoseAlertRequest request = UpdateGlucoseAlertRequest.builder()
                .highThreshold(new BigDecimal("300.0"))
                .build();

        when(glucoseAlertService.updateGlucoseAlert(anyLong(), anyLong(), any(UpdateGlucoseAlertRequest.class)))
                .thenReturn(testAlertDTO);

        mockMvc.perform(put("/api/v1/pets/1/alerts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete glucose alert successfully")
    void testDeleteGlucoseAlertSuccess() throws Exception {
        doNothing().when(glucoseAlertService).deleteGlucoseAlert(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/pets/1/alerts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 400 when creating alert without required alertType")
    void testCreateGlucoseAlertValidationFailure() throws Exception {
        CreateGlucoseAlertRequest request = CreateGlucoseAlertRequest.builder()
                .highThreshold(new BigDecimal("250.0"))
                .build();

        mockMvc.perform(post("/api/v1/pets/1/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
