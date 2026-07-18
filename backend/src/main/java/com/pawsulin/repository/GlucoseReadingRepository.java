package com.pawsulin.repository;

import com.pawsulin.entity.GlucoseReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GlucoseReadingRepository extends JpaRepository<GlucoseReading, Long> {
    Page<GlucoseReading> findByPetIdAndIsActiveTrue(Long petId, Pageable pageable);
    List<GlucoseReading> findByPetIdAndReadingTimeBetween(Long petId, LocalDateTime startTime, LocalDateTime endTime);
    Page<GlucoseReading> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
}
