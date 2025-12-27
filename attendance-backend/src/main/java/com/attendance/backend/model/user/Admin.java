package com.attendance.backend.model.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;

    private boolean enabled = true;
}
