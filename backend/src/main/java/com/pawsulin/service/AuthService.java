package com.pawsulin.service;

import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.LoginRequest;
import com.pawsulin.dto.auth.RegisterRequest;
import com.pawsulin.dto.auth.GoogleAuthRequest;
import com.pawsulin.dto.auth.AuthResponse;
import com.pawsulin.entity.User;
import com.pawsulin.exception.DuplicateResourceException;
import com.pawsulin.mapper.UserMapper;
import com.pawsulin.repository.UserRepository;
import com.pawsulin.security.GoogleTokenVerifier;
import com.pawsulin.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        return userMapper.toDTO(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        log.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        GoogleTokenVerifier.GoogleUserProfile profile = googleTokenVerifier.verifyIdToken(request.getIdToken());

        User user = userRepository.findByEmail(profile.email())
                .orElseGet(() -> registerGoogleUser(profile));

        String accessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        log.info("User authenticated with Google successfully: {}", user.getEmail());

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getRole().name());
    }

    public UserDTO getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }

    public UserDTO updateProfile(Long userId, UserDTO updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        user = userRepository.save(user);
        log.info("User profile updated: {}", user.getEmail());

        return userMapper.toDTO(user);
    }
    private User registerGoogleUser(GoogleTokenVerifier.GoogleUserProfile profile) {
        User user = User.builder()
                .email(profile.email())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .firstName(profile.firstName())
                .lastName(profile.lastName())
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered with Google successfully: {}", savedUser.getEmail());
        return savedUser;
    }
}
