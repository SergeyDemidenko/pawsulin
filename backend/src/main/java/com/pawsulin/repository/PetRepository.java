package com.pawsulin.repository;

import com.pawsulin.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserId(Long userId);
    Page<Pet> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
}
