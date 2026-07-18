package com.pawsulin.repository;

import com.pawsulin.entity.InsulinLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InsulinLogRepository extends JpaRepository<InsulinLog, Long> {
    Page<InsulinLog> findByPetIdAndIsActiveTrue(Long petId, Pageable pageable);
    List<InsulinLog> findByPetIdAndInjectionTimeBetween(Long petId, LocalDateTime startTime, LocalDateTime endTime);
    Page<InsulinLog> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
}
