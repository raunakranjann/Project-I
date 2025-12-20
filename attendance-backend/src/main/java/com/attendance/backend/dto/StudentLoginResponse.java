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
    private String name;
    private String token;
    private String message;

    // ============================
    // STATIC HELPERS
    // ============================

    public static StudentLoginResponse success(
            Long studentId,
            String name,
            String token
    ) {
        return new StudentLoginResponse(
                true,
                studentId,
                name,
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
