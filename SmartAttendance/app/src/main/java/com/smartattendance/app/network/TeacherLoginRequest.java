package com.smartattendance.app.network;

public class TeacherLoginRequest {

    private String username;
    private String password;

    public TeacherLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Needed by Retrofit (do not remove)
    public TeacherLoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
