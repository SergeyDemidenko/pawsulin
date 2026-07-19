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

@DisplayName("FacebookTokenVerifier Tests")
class FacebookTokenVerifierTest {

    @Test
    @DisplayName("Should reject verification when Facebook app ID is not configured")
    void testVerifyAccessTokenWithoutConfiguredAppId() {
        RestClient.Builder restClientBuilder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        FacebookTokenVerifier verifier = new FacebookTokenVerifier(restClientBuilder);
        ReflectionTestUtils.setField(verifier, "facebookAppId", " ");

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> verifier.verifyAccessToken("facebook-access-token"));

        assertEquals("Facebook sign-in is not configured", exception.getMessage());
        verifyNoInteractions(restClient);
    }
}
