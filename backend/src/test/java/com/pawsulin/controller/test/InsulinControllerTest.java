package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.InsulinController;
import com.pawsulin.dto.CreateInsulinLogRequest;
import com.pawsulin.dto.InsulinLogDTO;
import com.pawsulin.dto.UpdateInsulinLogRequest;
import com.pawsulin.security.UserPrincipal;
import com.pawsulin.service.InsulinService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InsulinController Tests")
class InsulinControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InsulinService insulinService;

    @InjectMocks
    private InsulinController insulinController;

    private ObjectMapper objectMapper;
    private InsulinLogDTO testLogDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(insulinController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "test@example.com", "password", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PET_OWNER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testLogDTO = InsulinLogDTO.builder()
                .id(1L)
                .petId(1L)
                .userId(1L)
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.now())
                .batchNumber("BATCH001")
                .expirationDate(LocalDate.now().plusMonths(6))
                .notes("Morning dose")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create insulin log successfully")
    void testCreateInsulinLogSuccess() throws Exception {
        CreateInsulinLogRequest request = CreateInsulinLogRequest.builder()
                .insulinType("Lantus")
                .amountUnits(new BigDecimal("5.0"))
                .injectionTime(LocalDateTime.now())
                .build();

        when(insulinService.createInsulinLog(anyLong(), anyLong(), any(CreateInsulinLogRequest.class)))
                .thenReturn(testLogDTO);

        mockMvc.perform(post("/api/v1/pets/1/insulin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.insulinType").value("Lantus"))
                .andExpect(jsonPath("$.amountUnits").value(5.0));
    }

    @Test
    @DisplayName("Should get insulin log by id successfully")
    void testGetInsulinLogByIdSuccess() throws Exception {
        when(insulinService.getInsulinLogById(anyLong(), anyLong()))
                .thenReturn(testLogDTO);

        mockMvc.perform(get("/api/v1/pets/1/insulin/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.insulinType").value("Lantus"));
    }

    @Test
    @DisplayName("Should get paginated insulin logs successfully")
    void testGetInsulinLogsSuccess() throws Exception {
        Page<InsulinLogDTO> logsPage = new PageImpl<>(List.of(testLogDTO), PageRequest.of(0, 10), 1);
        when(insulinService.getInsulinLogsByPetId(anyLong(), anyLong(), any()))
                .thenReturn(logsPage);

        mockMvc.perform(get("/api/v1/pets/1/insulin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should get insulin logs by date range successfully")
    void testGetInsulinLogsByDateRangeSuccess() throws Exception {
        when(insulinService.getInsulinLogsByDateRange(anyLong(), anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(testLogDTO));

        mockMvc.perform(get("/api/v1/pets/1/insulin/range")
                .param("startTime", "2026-07-01T00:00:00")
                .param("endTime", "2026-07-18T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].insulinType").value("Lantus"));
    }

    @Test
    @DisplayName("Should update insulin log successfully")
    void testUpdateInsulinLogSuccess() throws Exception {
        UpdateInsulinLogRequest request = UpdateInsulinLogRequest.builder()
                .amountUnits(new BigDecimal("8.0"))
                .build();

        when(insulinService.updateInsulinLog(anyLong(), anyLong(), any(UpdateInsulinLogRequest.class)))
                .thenReturn(testLogDTO);

        mockMvc.perform(put("/api/v1/pets/1/insulin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete insulin log successfully")
    void testDeleteInsulinLogSuccess() throws Exception {
        doNothing().when(insulinService).deleteInsulinLog(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/pets/1/insulin/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
