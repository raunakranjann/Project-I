package com.rentconnect.backend.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String email;
    private String password;
    private String role; // "LANDLORD" or "TENANT"
    private String phone;
}