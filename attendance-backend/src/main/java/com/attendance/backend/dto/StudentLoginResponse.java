package com.attendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLoginResponse {

    private boolean success;
    private Long studentId;
    private String studentName; // 🔴 FIXED (was name)
    private String token;
    private String message;

    // ============================
    // STATIC HELPERS
    // ============================

    public static StudentLoginResponse success(
            Long studentId,
            String studentName,
            String token
    ) {
        return new StudentLoginResponse(
                true,
                studentId,
                studentName,
                token,
                "Login successful"
        );
    }

    public static StudentLoginResponse failed(String message) {
        return new StudentLoginResponse(
                false,
                null,
                null,
                null,
                message
        );
    }
}
