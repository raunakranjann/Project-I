package com.smartattendance.app.network;

public class TeacherLoginResponse {

    private boolean success;
    private Long teacherId;
    private String teacherName;
    private String token;   // ✅ JWT
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
