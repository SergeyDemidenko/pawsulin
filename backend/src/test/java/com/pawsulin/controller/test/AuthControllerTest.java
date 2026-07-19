package com.pawsulin.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.controller.AuthController;
import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.LoginRequest;
import com.pawsulin.dto.auth.RegisterRequest;
import com.pawsulin.dto.auth.GoogleAuthRequest;
import com.pawsulin.dto.auth.AuthResponse;
import com.pawsulin.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        UserDTO expectedResponse = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("PET_OWNER")
                .isActive(true)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("Should authenticate with Google successfully")
    void testGoogleLoginSuccess() throws Exception {
        GoogleAuthRequest request = GoogleAuthRequest.builder()
                .idToken("google-id-token")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .userId(1L)
                .email("test@example.com")
                .role("PET_OWNER")
                .build();

        when(authService.loginWithGoogle(any(GoogleAuthRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should login user successfully")
    void testLoginSuccess() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .userId(1L)
                .email("test@example.com")
                .role("PET_OWNER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
