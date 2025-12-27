package com.attendance.backend.model.academics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "academic_sessions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"startYear", "endYear"})
        }
)
public class AcademicSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer startYear;   // 2021

    @Column(nullable = false)
    private Integer endYear;     // 2025

    @Column(nullable = false, unique = true)
    private String displayName;  // 2021-2025
}
