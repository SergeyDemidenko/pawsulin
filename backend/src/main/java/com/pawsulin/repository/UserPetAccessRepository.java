package com.pawsulin.repository;

import com.pawsulin.entity.UserPetAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPetAccessRepository extends JpaRepository<UserPetAccess, Long> {
    List<UserPetAccess> findByUserId(Long userId);
    List<UserPetAccess> findByPetId(Long petId);
    Optional<UserPetAccess> findByUserIdAndPetId(Long userId, Long petId);
}
