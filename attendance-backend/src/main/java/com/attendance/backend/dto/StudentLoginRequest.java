package com.attendance.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentLoginRequest {

    private String rollNo;     // ✅ FIXED
    private String password;
    private String deviceId; // ✅ DEVICE BINDING
}
