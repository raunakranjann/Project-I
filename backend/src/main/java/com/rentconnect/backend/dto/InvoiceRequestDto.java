package com.rentconnect.backend.dto;

import lombok.Data;

@Data
public class InvoiceRequestDto {
    private Long allocationId;
    private Double currentMeterReading;
    private Double unitRate; // e.g., 10.0 per unit
}