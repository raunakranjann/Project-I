package com.rentconnect.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(unique = true, nullable = false)
    private String roomNo;

    private Integer floorNo;

    @Column(nullable = false)
    private Double baseRent;

    private boolean isOccupied = false;
}