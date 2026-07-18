package com.pawsulin.dto;

import com.pawsulin.entity.UserPetAccess;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for granting or updating shared pet access.
 * Contains JSR-303 validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetAccessRequest {

    /**
     * The ID of the user to share pet access with.
     * Required when granting access to another user.
     */
    @NotNull(message = "Target user ID is required")
    private Long targetUserId;

    /**
     * The access level to grant (OWNER, EDITOR, VIEWER).
     */
    @NotNull(message = "Access level is required")
    private UserPetAccess.AccessLevel accessLevel;
}
