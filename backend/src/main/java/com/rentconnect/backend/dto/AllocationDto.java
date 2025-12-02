package com.rentconnect.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AllocationDto {
    private Long tenantId; // The User ID of the tenant
    private Long roomId;   // The Room ID to assign
    private LocalDate startDate;
    private LocalDate endDate; // Optional
}