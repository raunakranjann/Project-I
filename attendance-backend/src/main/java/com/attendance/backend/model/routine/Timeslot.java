package com.attendance.backend.model.routine;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "period_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int periodNo;

    private LocalTime startTime;
    private LocalTime endTime;

    // 🔑 IMPORTANT
    @Column(nullable = false)
    private boolean active = true;
}
