package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class TeacherLoginRequest {

    // ✅ Explicit JSON mapping (prevents field-name issues)
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    // ✅ Required by Gson / Retrofit
    public TeacherLoginRequest() {
    }

    // ✅ Used when sending login request
    public TeacherLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ---------- GETTERS ----------
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // ---------- DEBUG ----------
    @Override
    public String toString() {
        return "TeacherLoginRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
