package com.attendance.backend.model.routine.repository;

import com.attendance.backend.model.routine.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {

    // ===============================
    // ACTIVE TIMESLOTS ONLY (CORE)
    // ===============================
    List<Timeslot> findByActiveTrueOrderByPeriodNoAsc();

    Optional<Timeslot> findByPeriodNoAndActiveTrue(int periodNo);

    // ===============================
    // ❌ DO NOT USE ANYMORE
    // ===============================
    // deleteAll()  → REMOVED (FK SAFE)
    // findAll()    → DO NOT USE IN LOGIC
}
