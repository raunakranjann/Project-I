package com.rentconnect.backend.dto;

import lombok.Data;

@Data
public class RoomDto {
    private String roomNo;
    private Integer floorNo;
    private Double baseRent;
}