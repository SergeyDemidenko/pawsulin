package com.pawsulin.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawsulin.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Locale;

@Component
public class GoogleTokenVerifier {

    private static final int MAX_NAME_LENGTH = 100;

    private final RestClient restClient;

    @Value("${app.google.client-id:}")
    private String googleClientId;

    public GoogleTokenVerifier(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://oauth2.googleapis.com")
                .build();
    }

    public GoogleUserProfile verifyIdToken(String idToken) {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new InvalidRequestException("Google sign-in is not configured");
        }

        GoogleTokenInfo tokenInfo;
        try {
            tokenInfo = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/tokeninfo")
                            .queryParam("id_token", idToken)
                            .build())
                    .retrieve()
                    .body(GoogleTokenInfo.class);
        } catch (RestClientException exception) {
            throw new InvalidRequestException("Invalid Google account token", exception);
        }

        if (tokenInfo == null
                || tokenInfo.audience() == null
                || !googleClientId.equals(tokenInfo.audience())) {
            throw new InvalidRequestException("Google token audience does not match this application");
        }

        if (tokenInfo.issuer() == null
                || !("accounts.google.com".equals(tokenInfo.issuer())
                || "https://accounts.google.com".equals(tokenInfo.issuer()))) {
            throw new InvalidRequestException("Google token issuer is invalid");
        }

        if (!"true".equalsIgnoreCase(tokenInfo.emailVerified())) {
            throw new InvalidRequestException("Google account email is not verified");
        }

        if (tokenInfo.email() == null || tokenInfo.email().isBlank()) {
            throw new InvalidRequestException("Google account email is missing");
        }

        String email = tokenInfo.email().trim().toLowerCase(Locale.ROOT);
        String firstName = resolveFirstName(tokenInfo);
        String lastName = resolveLastName(tokenInfo);

        return new GoogleUserProfile(email, firstName, lastName);
    }

    private String resolveFirstName(GoogleTokenInfo tokenInfo) {
        if (tokenInfo.givenName() != null && !tokenInfo.givenName().isBlank()) {
            return truncate(tokenInfo.givenName().trim());
        }
        if (tokenInfo.name() != null && !tokenInfo.name().isBlank()) {
            String[] parts = tokenInfo.name().trim().split("\\s+", 2);
            if (parts.length > 0 && !parts[0].isBlank()) {
                return truncate(parts[0]);
            }
        }
        return truncate(tokenInfo.email().split("@", 2)[0]);
    }

    private String resolveLastName(GoogleTokenInfo tokenInfo) {
        if (tokenInfo.familyName() != null && !tokenInfo.familyName().isBlank()) {
            return truncate(tokenInfo.familyName().trim());
        }
        if (tokenInfo.name() != null && !tokenInfo.name().isBlank()) {
            String[] parts = tokenInfo.name().trim().split("\\s+", 2);
            if (parts.length > 1 && !parts[1].isBlank()) {
                return truncate(parts[1]);
            }
        }
        return "Google";
    }

    private String truncate(String value) {
        return value.length() <= MAX_NAME_LENGTH ? value : value.substring(0, MAX_NAME_LENGTH);
    }

    public record GoogleUserProfile(String email, String firstName, String lastName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GoogleTokenInfo(
            @JsonProperty("aud") String audience,
            @JsonProperty("iss") String issuer,
            @JsonProperty("email") String email,
            @JsonProperty("email_verified") String emailVerified,
            @JsonProperty("given_name") String givenName,
            @JsonProperty("family_name") String familyName,
            @JsonProperty("name") String name
    ) {
    }
}
