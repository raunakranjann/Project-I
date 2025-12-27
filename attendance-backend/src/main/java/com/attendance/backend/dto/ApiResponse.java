package com.attendance.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    private ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // =============================
    // SUCCESS WITH DATA
    // =============================
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data);
    }

    // =============================
    // SUCCESS WITH MESSAGE ONLY
    // =============================
    public static <T> ApiResponse<T> successMessage(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }

    // =============================
    // FAILURE
    // =============================
    public static <T> ApiResponse<T> failed(String message) {
        return new ApiResponse<>("FAIL", message, null);
    }
}
