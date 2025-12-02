package com.rentconnect.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "allocations")
public class Allocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User tenant; // Links to the User table

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;   // Links to the Room table

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive = true;
}