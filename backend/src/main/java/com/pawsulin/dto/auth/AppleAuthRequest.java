package com.pawsulin.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleAuthRequest {
    @NotBlank(message = "Apple identity token is required")
    private String idToken;

    // Apple only provides name on the first sign-in; the frontend must forward it
    private String firstName;
    private String lastName;
}
