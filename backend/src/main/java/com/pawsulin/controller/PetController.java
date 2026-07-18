package com.pawsulin.controller;

import com.pawsulin.dto.CreatePetRequest;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UpdatePetRequest;
import com.pawsulin.service.PetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@Slf4j
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody CreatePetRequest request) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Create pet request for user: {}", userId);
        PetDTO petDTO = petService.createPet(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(petDTO);
    }

    @GetMapping("/{petId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long petId) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get pet request for petId: {} by user: {}", petId, userId);
        PetDTO petDTO = petService.getPetById(petId, userId);
        return ResponseEntity.ok(petDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Page<PetDTO>> getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get all pets request for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<PetDTO> pets = petService.getPetsByUserId(userId, pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<List<PetDTO>> getAllPetsNonPaginated() {
        Long userId = extractUserIdFromAuthentication();
        log.info("Get all pets (non-paginated) request for user: {}", userId);
        List<PetDTO> pets = petService.getAllPetsByUserId(userId);
        return ResponseEntity.ok(pets);
    }

    @PutMapping("/{petId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<PetDTO> updatePet(
            @PathVariable Long petId,
            @Valid @RequestBody UpdatePetRequest request) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Update pet request for petId: {} by user: {}", petId, userId);
        PetDTO petDTO = petService.updatePet(petId, userId, request);
        return ResponseEntity.ok(petDTO);
    }

    @DeleteMapping("/{petId}")
    @PreAuthorize("hasRole('PET_OWNER') or hasRole('VETERINARIAN')")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId) {
        Long userId = extractUserIdFromAuthentication();
        log.info("Delete pet request for petId: {} by user: {}", petId, userId);
        petService.deletePet(petId, userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // In production, extract from JWT token
        // For now, returning placeholder
        return 1L;
    }
}
