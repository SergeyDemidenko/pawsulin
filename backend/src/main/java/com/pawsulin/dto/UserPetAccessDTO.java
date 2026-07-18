package com.pawsulin.dto;

import com.pawsulin.entity.UserPetAccess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object for a user-pet access record.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetAccessDTO {
    private Long id;
    private Long userId;
    private Long petId;
    private UserPetAccess.AccessLevel accessLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
