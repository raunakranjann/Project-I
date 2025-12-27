package com.attendance.backend.model.routine.repository;

import com.attendance.backend.model.routine.ScheduleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleConfigRepository
        extends JpaRepository<ScheduleConfig, Long> {

    Optional<ScheduleConfig> findByActiveTrue();
}
