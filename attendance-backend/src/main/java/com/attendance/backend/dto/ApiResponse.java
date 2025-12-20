package com.attendance.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse {

    private String status;
    private String message;

    private ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // ✅ SUCCESS helper
    public static ApiResponse success(String message) {
        return new ApiResponse("SUCCESS", message);
    }

    // ✅ FAILURE helper (THIS WAS MISSING)
    public static ApiResponse failed(String message) {
        return new ApiResponse("FAIL", message);
    }
}
