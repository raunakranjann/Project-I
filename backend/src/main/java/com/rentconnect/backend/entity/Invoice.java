package com.rentconnect.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne
    @JoinColumn(name = "allocation_id", nullable = false)
    private Allocation allocation; // Links to the Allocation table

    private Double amount;
    private LocalDate billDate = LocalDate.now();
    private LocalDate dueDate;

    private String status; // e.g., "PENDING", "PAID"

    // Smart Meter Logic Fields
    private Double electricityUsage; // Units consumed
    private Double currentReading;
    private Double previousReading;
}