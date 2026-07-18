package com.pawsulin.security.test;

import com.pawsulin.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKeyForJwtTokenProviderTestingPurposeOnly1234567890abcdef");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationInMs", 604800000L);
    }

    @Test
    @DisplayName("Should generate valid token")
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should extract userId from token")
    void testGetUserIdFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("Should extract email from token")
    void testGetEmailFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "test@example.com");

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertEquals("test@example.com", email);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate refresh token")
    void testGenerateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken(1L, "test@example.com");

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
    }
}
