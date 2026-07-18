package com.pawsulin.repository;

import com.pawsulin.entity.GlucoseAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GlucoseAlertRepository extends JpaRepository<GlucoseAlert, Long> {
    List<GlucoseAlert> findByPetIdAndIsEnabledTrue(Long petId);
    List<GlucoseAlert> findByUserIdAndIsEnabledTrue(Long userId);
    List<GlucoseAlert> findByPetId(Long petId);
}
