package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.UserPetAccessController;
import com.pawsulin.dto.UserPetAccessDTO;
import com.pawsulin.dto.UserPetAccessRequest;
import com.pawsulin.entity.UserPetAccess;
import com.pawsulin.exception.AccessDeniedException;
import com.pawsulin.exception.GlobalExceptionHandler;
import com.pawsulin.security.UserPrincipal;
import com.pawsulin.service.UserPetAccessService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPetAccessController Tests")
class UserPetAccessControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserPetAccessService userPetAccessService;

    @InjectMocks
    private UserPetAccessController userPetAccessController;

    private ObjectMapper objectMapper;
    private UserPetAccessDTO testAccessDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userPetAccessController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "owner@example.com", "password", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PET_OWNER")));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testAccessDTO = UserPetAccessDTO.builder()
                .id(2L)
                .userId(2L)
                .petId(1L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should grant access successfully")
    void testGrantAccessSuccess() throws Exception {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(userPetAccessService.grantAccess(anyLong(), anyLong(), any(UserPetAccessRequest.class)))
                .thenReturn(testAccessDTO);

        mockMvc.perform(post("/api/v1/pets/1/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessLevel").value("VIEWER"))
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    @DisplayName("Should return paginated access list for pet")
    void testGetAccessByPetSuccess() throws Exception {
        Page<UserPetAccessDTO> accessPage = new PageImpl<>(
                List.of(testAccessDTO), PageRequest.of(0, 10), 1);

        when(userPetAccessService.getAccessByPetId(anyLong(), anyLong(), any()))
                .thenReturn(accessPage);

        mockMvc.perform(get("/api/v1/pets/1/access")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should get specific access record by id")
    void testGetAccessByIdSuccess() throws Exception {
        when(userPetAccessService.getAccessById(anyLong(), anyLong()))
                .thenReturn(testAccessDTO);

        mockMvc.perform(get("/api/v1/pets/1/access/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.accessLevel").value("VIEWER"));
    }

    @Test
    @DisplayName("Should update access level successfully")
    void testUpdateAccessLevelSuccess() throws Exception {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.EDITOR)
                .build();

        UserPetAccessDTO updatedDTO = UserPetAccessDTO.builder()
                .id(2L)
                .userId(2L)
                .petId(1L)
                .accessLevel(UserPetAccess.AccessLevel.EDITOR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userPetAccessService.updateAccessLevel(anyLong(), anyLong(), any(UserPetAccessRequest.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/v1/pets/1/access/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessLevel").value("EDITOR"));
    }

    @Test
    @DisplayName("Should revoke access successfully")
    void testRevokeAccessSuccess() throws Exception {
        doNothing().when(userPetAccessService).revokeAccess(anyLong(), anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/pets/1/access/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return paginated list of all pets user has access to")
    void testGetUserPetAccessSuccess() throws Exception {
        Page<UserPetAccessDTO> accessPage = new PageImpl<>(
                List.of(testAccessDTO), PageRequest.of(0, 10), 1);

        when(userPetAccessService.getAccessByUserId(anyLong(), any()))
                .thenReturn(accessPage);

        mockMvc.perform(get("/api/v1/user/pets/access")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should return 403 when access is denied")
    void testGrantAccessForbidden() throws Exception {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(userPetAccessService.grantAccess(anyLong(), anyLong(), any(UserPetAccessRequest.class)))
                .thenThrow(new AccessDeniedException("Only the pet owner can grant access to other users"));

        mockMvc.perform(post("/api/v1/pets/1/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 400 when request body is missing required fields")
    void testGrantAccessValidationFailure() throws Exception {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .build(); // Missing targetUserId and accessLevel

        mockMvc.perform(post("/api/v1/pets/1/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
