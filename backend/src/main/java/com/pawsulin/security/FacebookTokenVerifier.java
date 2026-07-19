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
public class FacebookTokenVerifier {

    private static final int MAX_NAME_LENGTH = 100;

    private final RestClient restClient;

    @Value("${app.facebook.app-id:}")
    private String facebookAppId;

    public FacebookTokenVerifier(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://graph.facebook.com")
                .build();
    }

    public FacebookUserProfile verifyAccessToken(String accessToken) {
        if (facebookAppId == null || facebookAppId.isBlank()) {
            throw new InvalidRequestException("Facebook sign-in is not configured");
        }

        FacebookUserInfo userInfo;
        try {
            userInfo = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/me")
                            .queryParam("fields", "id,email,first_name,last_name,name")
                            .queryParam("access_token", accessToken)
                            .build())
                    .retrieve()
                    .body(FacebookUserInfo.class);
        } catch (RestClientException exception) {
            throw new InvalidRequestException("Invalid Facebook account token", exception);
        }

        if (userInfo == null || userInfo.id() == null || userInfo.id().isBlank()) {
            throw new InvalidRequestException("Invalid Facebook account token");
        }

        if (userInfo.email() == null || userInfo.email().isBlank()) {
            throw new InvalidRequestException("Facebook account email is missing");
        }

        String email = userInfo.email().trim().toLowerCase(Locale.ROOT);
        String emailLocalPart = email.split("@", 2)[0];
        String firstName = resolveFirstName(userInfo, emailLocalPart);
        String lastName = resolveLastName(userInfo);

        return new FacebookUserProfile(email, firstName, lastName);
    }

    private String resolveFirstName(FacebookUserInfo userInfo, String emailLocalPart) {
        if (userInfo.firstName() != null && !userInfo.firstName().isBlank()) {
            return truncate(userInfo.firstName().trim());
        }
        if (userInfo.name() != null && !userInfo.name().isBlank()) {
            String[] parts = userInfo.name().trim().split("\\s+", 2);
            if (parts.length > 0 && !parts[0].isBlank()) {
                return truncate(parts[0]);
            }
        }
        return truncate(emailLocalPart);
    }

    private String resolveLastName(FacebookUserInfo userInfo) {
        if (userInfo.lastName() != null && !userInfo.lastName().isBlank()) {
            return truncate(userInfo.lastName().trim());
        }
        if (userInfo.name() != null && !userInfo.name().isBlank()) {
            String[] parts = userInfo.name().trim().split("\\s+", 2);
            if (parts.length > 1 && !parts[1].isBlank()) {
                return truncate(parts[1]);
            }
        }
        return "Facebook";
    }

    private String truncate(String value) {
        return value.length() <= MAX_NAME_LENGTH ? value : value.substring(0, MAX_NAME_LENGTH);
    }

    public record FacebookUserProfile(String email, String firstName, String lastName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record FacebookUserInfo(
            @JsonProperty("id") String id,
            @JsonProperty("email") String email,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("name") String name
    ) {
    }
}
