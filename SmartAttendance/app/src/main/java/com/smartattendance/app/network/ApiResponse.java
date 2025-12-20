package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Required by Gson
    public ApiResponse() {
    }

    // Optional convenience constructor
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Helper methods (safe to ignore if not used)
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
