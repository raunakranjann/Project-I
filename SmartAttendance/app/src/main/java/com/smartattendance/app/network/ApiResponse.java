package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    // ✅ MUST MATCH BACKEND JSON
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Required by Gson
    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // ---------- GETTERS ----------
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // ---------- DEBUG ----------
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
