package com.pawsulin.service.test;

import com.pawsulin.dto.UserPetAccessDTO;
import com.pawsulin.dto.UserPetAccessRequest;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.entity.UserPetAccess;
import com.pawsulin.exception.AccessDeniedException;
import com.pawsulin.exception.DuplicateResourceException;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserPetAccessRepository;
import com.pawsulin.repository.UserRepository;
import com.pawsulin.service.UserPetAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPetAccessService Tests")
class UserPetAccessServiceTest {

    @Mock
    private UserPetAccessRepository userPetAccessRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPetAccessService userPetAccessService;

    private User ownerUser;
    private User targetUser;
    private Pet testPet;
    private UserPetAccess ownerAccess;
    private UserPetAccess viewerAccess;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder()
                .id(1L)
                .email("owner@example.com")
                .firstName("Owner")
                .lastName("User")
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        targetUser = User.builder()
                .id(2L)
                .email("target@example.com")
                .firstName("Target")
                .lastName("User")
                .role(User.UserRole.PET_OWNER)
                .isActive(true)
                .build();

        testPet = Pet.builder()
                .id(1L)
                .user(ownerUser)
                .name("Fluffy")
                .species("Cat")
                .breed("Persian")
                .ageYears(3)
                .weightKg(new BigDecimal("4.5"))
                .diabetesType("Type 1")
                .isActive(true)
                .build();

        ownerAccess = UserPetAccess.builder()
                .id(1L)
                .user(ownerUser)
                .pet(testPet)
                .accessLevel(UserPetAccess.AccessLevel.OWNER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        viewerAccess = UserPetAccess.builder()
                .id(2L)
                .user(targetUser)
                .pet(testPet)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ---- grantAccess tests ----

    @Test
    @DisplayName("Should grant access successfully")
    void testGrantAccessSuccess() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userPetAccessRepository.existsByUserIdAndPetId(2L, 1L)).thenReturn(false);
        when(userPetAccessRepository.save(any(UserPetAccess.class))).thenReturn(viewerAccess);

        UserPetAccessDTO result = userPetAccessService.grantAccess(1L, 1L, request);

        assertNotNull(result);
        assertEquals(UserPetAccess.AccessLevel.VIEWER, result.getAccessLevel());
        assertEquals(2L, result.getUserId());
        verify(userPetAccessRepository, times(1)).save(any(UserPetAccess.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when pet not found during grant access")
    void testGrantAccessPetNotFound() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userPetAccessService.grantAccess(99L, 1L, request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when target user not found during grant access")
    void testGrantAccessTargetUserNotFound() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(99L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userPetAccessService.grantAccess(1L, 1L, request));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner tries to grant access")
    void testGrantAccessNotOwner() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(3L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(2L, 1L)).thenReturn(Optional.of(viewerAccess));

        assertThrows(AccessDeniedException.class,
                () -> userPetAccessService.grantAccess(1L, 2L, request));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when granting access to self")
    void testGrantAccessSelfAccessDenied() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(1L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));

        assertThrows(IllegalArgumentException.class,
                () -> userPetAccessService.grantAccess(1L, 1L, request));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when access already exists")
    void testGrantAccessDuplicateError() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.VIEWER)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));
        when(userPetAccessRepository.existsByUserIdAndPetId(2L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> userPetAccessService.grantAccess(1L, 1L, request));
    }

    // ---- getAccessByPetId tests ----

    @Test
    @DisplayName("Should get access records by pet id successfully")
    void testGetAccessByPetIdSuccess() {
        Page<UserPetAccess> accessPage = new PageImpl<>(
                List.of(ownerAccess, viewerAccess), PageRequest.of(0, 10), 2);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userPetAccessRepository.findByPetId(1L, PageRequest.of(0, 10))).thenReturn(accessPage);

        Page<UserPetAccessDTO> result = userPetAccessService.getAccessByPetId(1L, 1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    // ---- getAccessByUserId tests ----

    @Test
    @DisplayName("Should get access records by user id successfully")
    void testGetAccessByUserIdSuccess() {
        Page<UserPetAccess> accessPage = new PageImpl<>(
                List.of(viewerAccess), PageRequest.of(0, 10), 1);

        when(userPetAccessRepository.findByUserId(2L, PageRequest.of(0, 10))).thenReturn(accessPage);

        Page<UserPetAccessDTO> result = userPetAccessService.getAccessByUserId(2L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(UserPetAccess.AccessLevel.VIEWER, result.getContent().get(0).getAccessLevel());
    }

    // ---- updateAccessLevel tests ----

    @Test
    @DisplayName("Should update access level successfully")
    void testUpdateAccessLevelSuccess() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.EDITOR)
                .build();

        UserPetAccess updatedAccess = UserPetAccess.builder()
                .id(2L)
                .user(targetUser)
                .pet(testPet)
                .accessLevel(UserPetAccess.AccessLevel.EDITOR)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userPetAccessRepository.findById(2L)).thenReturn(Optional.of(viewerAccess));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userPetAccessRepository.save(any(UserPetAccess.class))).thenReturn(updatedAccess);

        UserPetAccessDTO result = userPetAccessService.updateAccessLevel(2L, 1L, request);

        assertNotNull(result);
        assertEquals(UserPetAccess.AccessLevel.EDITOR, result.getAccessLevel());
        verify(userPetAccessRepository, times(1)).save(any(UserPetAccess.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner tries to update access level")
    void testUpdateAccessLevelNotOwner() {
        UserPetAccessRequest request = UserPetAccessRequest.builder()
                .targetUserId(2L)
                .accessLevel(UserPetAccess.AccessLevel.EDITOR)
                .build();

        when(userPetAccessRepository.findById(2L)).thenReturn(Optional.of(viewerAccess));
        when(userPetAccessRepository.findByUserIdAndPetId(2L, 1L)).thenReturn(Optional.of(viewerAccess));

        assertThrows(AccessDeniedException.class,
                () -> userPetAccessService.updateAccessLevel(2L, 2L, request));
    }

    // ---- revokeAccess tests ----

    @Test
    @DisplayName("Should revoke access successfully")
    void testRevokeAccessSuccess() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));
        when(userPetAccessRepository.findByUserIdAndPetId(2L, 1L)).thenReturn(Optional.of(viewerAccess));

        userPetAccessService.revokeAccess(1L, 1L, 2L);

        verify(userPetAccessRepository, times(1)).delete(viewerAccess);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner tries to revoke access")
    void testRevokeAccessNotOwner() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userPetAccessRepository.findByUserIdAndPetId(2L, 1L)).thenReturn(Optional.of(viewerAccess));

        assertThrows(AccessDeniedException.class,
                () -> userPetAccessService.revokeAccess(1L, 2L, 1L));
    }

    // ---- hasAccess and getAccessLevel tests ----

    @Test
    @DisplayName("Should return true when user has access to pet")
    void testHasAccessTrue() {
        when(userPetAccessRepository.existsByUserIdAndPetId(1L, 1L)).thenReturn(true);

        assertTrue(userPetAccessService.hasAccess(1L, 1L));
    }

    @Test
    @DisplayName("Should return false when user does not have access to pet")
    void testHasAccessFalse() {
        when(userPetAccessRepository.existsByUserIdAndPetId(99L, 1L)).thenReturn(false);

        assertFalse(userPetAccessService.hasAccess(99L, 1L));
    }

    @Test
    @DisplayName("Should return correct access level for user")
    void testGetAccessLevelReturnsOwner() {
        when(userPetAccessRepository.findByUserIdAndPetId(1L, 1L)).thenReturn(Optional.of(ownerAccess));

        Optional<UserPetAccess.AccessLevel> level = userPetAccessService.getAccessLevel(1L, 1L);

        assertTrue(level.isPresent());
        assertEquals(UserPetAccess.AccessLevel.OWNER, level.get());
    }

    @Test
    @DisplayName("Should return empty Optional when user has no access to pet")
    void testGetAccessLevelEmpty() {
        when(userPetAccessRepository.findByUserIdAndPetId(99L, 1L)).thenReturn(Optional.empty());

        Optional<UserPetAccess.AccessLevel> level = userPetAccessService.getAccessLevel(99L, 1L);

        assertFalse(level.isPresent());
    }
}
