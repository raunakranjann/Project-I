package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class StudentLoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("name")
    private String studentName;

    @SerializedName("token")
    private String token;

    @SerializedName("message")
    private String message;

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
}
