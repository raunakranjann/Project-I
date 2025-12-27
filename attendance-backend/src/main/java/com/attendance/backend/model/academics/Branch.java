package com.attendance.backend.model.academics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "branches",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"code", "course_id"})
        }
)
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CSE, ECE, ME, HR, FIN, etc.
    @Column(nullable = false, length = 10)
    private String code;

    // Computer Science & Engineering, Electronics, etc.
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
