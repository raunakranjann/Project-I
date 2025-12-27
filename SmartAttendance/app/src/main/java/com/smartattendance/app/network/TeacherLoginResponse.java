package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class TeacherLoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("teacherId")
    private Long teacherId;

    @SerializedName("teacherName")
    private String teacherName;

    @SerializedName("token")
    private String token;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
