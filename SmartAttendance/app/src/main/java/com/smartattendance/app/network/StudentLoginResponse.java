package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class StudentLoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("studentName")
    private String studentName;

    @SerializedName("token")
    private String token; // ✅ JWT TOKEN

    @SerializedName("message")
    private String message;

    // Required empty constructor for Gson
    public StudentLoginResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "StudentLoginResponse{" +
                "success=" + success +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
