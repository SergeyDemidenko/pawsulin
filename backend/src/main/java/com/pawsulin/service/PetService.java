package com.pawsulin.service;

import com.pawsulin.dto.CreatePetRequest;
import com.pawsulin.dto.PetDTO;
import com.pawsulin.dto.UpdatePetRequest;
import com.pawsulin.entity.Pet;
import com.pawsulin.entity.User;
import com.pawsulin.exception.ResourceNotFoundException;
import com.pawsulin.mapper.PetMapper;
import com.pawsulin.repository.PetRepository;
import com.pawsulin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetMapper petMapper;

    public PetDTO createPet(Long userId, CreatePetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Pet pet = Pet.builder()
                .user(user)
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .ageYears(request.getAgeYears())
                .weightKg(request.getWeightKg())
                .diabetesType(request.getDiabetesType())
                .medicalNotes(request.getMedicalNotes())
                .isActive(true)
                .build();

        pet = petRepository.save(pet);
        log.info("Pet created successfully: {} for user: {}", pet.getId(), userId);
        return petMapper.toDTO(pet);
    }

    @Transactional(readOnly = true)
    public PetDTO getPetById(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access attempt to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        return petMapper.toDTO(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetDTO> getPetsByUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Page<Pet> pets = petRepository.findByUserIdAndIsActiveTrue(userId, pageable);
        log.info("Retrieved {} pets for user: {}", pets.getTotalElements(), userId);
        return pets.map(petMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PetDTO> getAllPetsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Pet> pets = petRepository.findByUserId(userId);
        log.info("Retrieved {} pets for user: {}", pets.size(), userId);
        return pets.stream()
                .filter(Pet::getIsActive)
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PetDTO updatePet(Long petId, Long userId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized update attempt to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        if (request.getName() != null) {
            pet.setName(request.getName());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getAgeYears() != null) {
            pet.setAgeYears(request.getAgeYears());
        }
        if (request.getWeightKg() != null) {
            pet.setWeightKg(request.getWeightKg());
        }
        if (request.getDiabetesType() != null) {
            pet.setDiabetesType(request.getDiabetesType());
        }
        if (request.getMedicalNotes() != null) {
            pet.setMedicalNotes(request.getMedicalNotes());
        }

        pet = petRepository.save(pet);
        log.info("Pet updated successfully: {} for user: {}", petId, userId);
        return petMapper.toDTO(pet);
    }

    public void deletePet(Long petId, Long userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));

        if (!pet.getUser().getId().equals(userId)) {
            log.warn("Unauthorized delete attempt to pet: {} by user: {}", petId, userId);
            throw new ResourceNotFoundException("Pet not found or unauthorized access");
        }

        pet.setIsActive(false);
        petRepository.save(pet);
        log.info("Pet deleted (soft delete) successfully: {} for user: {}", petId, userId);
    }

}
