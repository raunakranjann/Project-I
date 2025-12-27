package com.attendance.backend.model.routine;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "period_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Total periods per day (e.g. 7)
    @Column(nullable = false)
    private int totalPeriods;

    // Duration of each period in minutes (e.g. 50)
    @Column(nullable = false)
    private int durationMinutes;

    // Start time of first period (e.g. 09:00)
    @Column(nullable = false)
    private LocalTime dayStartTime;

    // Applies globally
    @Column(nullable = false)
    private boolean active;

    // =====================================================
    // DERIVED TIME HELPERS (REQUIRED)
    // =====================================================

    public LocalTime getStartTimeForPeriod(int periodNo) {
        if (periodNo < 1 || periodNo > totalPeriods) {
            throw new IllegalArgumentException(
                    "Invalid period number: " + periodNo
            );
        }

        return dayStartTime.plusMinutes(
                (long) (periodNo - 1) * durationMinutes
        );
    }

    public LocalTime getEndTimeForPeriod(int periodNo) {
        return getStartTimeForPeriod(periodNo)
                .plusMinutes(durationMinutes);
    }
}
