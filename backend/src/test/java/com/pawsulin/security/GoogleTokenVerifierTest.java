package com.pawsulin.security;

import com.pawsulin.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("GoogleTokenVerifier Tests")
class GoogleTokenVerifierTest {

    @Test
    @DisplayName("Should reject verification when Google client ID is not configured")
    void testVerifyIdTokenWithoutConfiguredClientId() {
        RestClient.Builder restClientBuilder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        GoogleTokenVerifier verifier = new GoogleTokenVerifier(restClientBuilder);
        ReflectionTestUtils.setField(verifier, "googleClientId", " ");

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> verifier.verifyIdToken("google-id-token"));

        assertEquals("Google sign-in is not configured", exception.getMessage());
        verifyNoInteractions(restClient);
    }
}
