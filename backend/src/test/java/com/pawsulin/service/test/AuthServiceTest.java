package com.pawsulin.service.test;

import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.LoginRequest;
import com.pawsulin.dto.auth.RegisterRequest;
import com.pawsulin.dto.auth.AuthResponse;
import com.pawsulin.entity.User;
import com.pawsulin.exception.DuplicateResourceException;
import com.pawsulin.mapper.UserMapper;
import com.pawsulin.repository.UserRepository;
import com.pawsulin.security.JwtTokenProvider;
import com.pawsulin.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        lenient().when(userMapper.toDTO(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return UserDTO.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .role(user.getRole() != null ? user.getRole().name() : null)
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .isActive(user.getIsActive())
                            .build();
                });
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering with existing email")
    void testRegisterWithDuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(testUser.getId(), testUser.getEmail())).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(testUser.getId(), testUser.getEmail())).thenReturn("refreshToken");

        AuthResponse result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        assertEquals(testUser.getId(), result.getUserId());
    }

    @Test
    @DisplayName("Should get current user successfully")
    void testGetCurrentUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDTO result = authService.getCurrentUser(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void testUpdateProfileSuccess() {
        UserDTO updateRequest = UserDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = authService.updateProfile(1L, updateRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
