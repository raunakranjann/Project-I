package com.attendance.backend.model.academics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code")
        }
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BTECH, BCA, BBA, MBA
    @Column(nullable = false, length = 10)
    private String code;

    // Bachelor of Technology, Bachelor of Computer Applications
    @Column(nullable = false)
    private String name;
}
