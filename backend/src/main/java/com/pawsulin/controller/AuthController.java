package com.pawsulin.controller;

import com.pawsulin.dto.UserDTO;
import com.pawsulin.dto.auth.LoginRequest;
import com.pawsulin.dto.auth.RegisterRequest;
import com.pawsulin.dto.auth.GoogleAuthRequest;
import com.pawsulin.dto.auth.AuthResponse;
import com.pawsulin.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for email: {}", request.getEmail());
        UserDTO userDTO = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        log.info("Google auth request received");
        AuthResponse authResponse = authService.loginWithGoogle(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Get profile request for userId: {}", userId);
        UserDTO userDTO = authService.getCurrentUser(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserDTO updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = extractUserIdFromAuthentication(authentication);
        log.info("Update profile request for userId: {}", userId);
        UserDTO userDTO = authService.updateProfile(userId, updateRequest);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("Logout request");
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // In a real scenario, this would extract userId from JWT token
        // For now, returning a placeholder
        return 1L;
    }
}
