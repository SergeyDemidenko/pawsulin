package com.pawsulin.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsulin.exception.InvalidRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class AppleTokenVerifier {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final int MAX_NAME_LENGTH = 100;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${app.apple.client-id:}")
    private String appleClientId;

    public AppleTokenVerifier(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder
                .baseUrl(APPLE_ISSUER)
                .build();
        this.objectMapper = objectMapper;
    }

    public AppleUserProfile verifyIdToken(String idToken, String firstName, String lastName) {
        if (appleClientId == null || appleClientId.isBlank()) {
            throw new InvalidRequestException("Apple sign-in is not configured");
        }

        Claims claims = parseAndVerifyClaims(idToken);

        String issuer = claims.getIssuer();
        if (!APPLE_ISSUER.equals(issuer)) {
            throw new InvalidRequestException("Apple token issuer is invalid");
        }

        String audience = claims.getAudience() != null ? claims.getAudience().stream().findFirst().orElse(null) : null;
        if (!appleClientId.equals(audience)) {
            throw new InvalidRequestException("Apple token audience does not match this application");
        }

        String email = (String) claims.get("email");
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("Apple account email is missing");
        }
        email = email.trim().toLowerCase(Locale.ROOT);

        String emailLocalPart = email.split("@", 2)[0];
        String resolvedFirstName = resolveFirstName(firstName, emailLocalPart);
        String resolvedLastName = resolveLastName(lastName);

        return new AppleUserProfile(email, resolvedFirstName, resolvedLastName);
    }

    private Claims parseAndVerifyClaims(String idToken) {
        String kid = extractKid(idToken);
        PublicKey publicKey = fetchApplePublicKey(kid);

        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();
        } catch (JwtException exception) {
            throw new InvalidRequestException("Invalid Apple identity token", exception);
        }
    }

    private String extractKid(String idToken) {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new InvalidRequestException("Invalid Apple identity token format");
        }
        try {
            byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
            @SuppressWarnings("unchecked")
            Map<String, Object> header = objectMapper.readValue(headerBytes, Map.class);
            Object kid = header.get("kid");
            if (!(kid instanceof String kidStr) || kidStr.isBlank()) {
                throw new InvalidRequestException("Apple identity token header is missing kid");
            }
            return kidStr;
        } catch (IOException | IllegalArgumentException exception) {
            throw new InvalidRequestException("Invalid Apple identity token header", exception);
        }
    }

    private PublicKey fetchApplePublicKey(String kid) {
        AppleJwksResponse jwks;
        try {
            jwks = restClient.get()
                    .uri("/auth/keys")
                    .retrieve()
                    .body(AppleJwksResponse.class);
        } catch (RestClientException exception) {
            throw new InvalidRequestException("Unable to fetch Apple public keys", exception);
        }

        if (jwks == null || jwks.keys() == null) {
            throw new InvalidRequestException("Unable to fetch Apple public keys");
        }

        AppleJwk jwk = jwks.keys().stream()
                .filter(k -> kid.equals(k.kid()))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Apple public key not found for kid: " + kid));

        return buildRsaPublicKey(jwk);
    }

    private PublicKey buildRsaPublicKey(AppleJwk jwk) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.n()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.e()));
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException exception) {
            throw new InvalidRequestException("Failed to construct Apple public key", exception);
        }
    }

    private String resolveFirstName(String firstName, String emailLocalPart) {
        if (firstName != null && !firstName.isBlank()) {
            return truncate(firstName.trim());
        }
        return truncate(emailLocalPart);
    }

    private String resolveLastName(String lastName) {
        if (lastName != null && !lastName.isBlank()) {
            return truncate(lastName.trim());
        }
        return "Apple";
    }

    private String truncate(String value) {
        return value.length() <= MAX_NAME_LENGTH ? value : value.substring(0, MAX_NAME_LENGTH);
    }

    public record AppleUserProfile(String email, String firstName, String lastName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AppleJwksResponse(
            @JsonProperty("keys") List<AppleJwk> keys
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AppleJwk(
            @JsonProperty("kid") String kid,
            @JsonProperty("n") String n,
            @JsonProperty("e") String e
    ) {
    }
}
