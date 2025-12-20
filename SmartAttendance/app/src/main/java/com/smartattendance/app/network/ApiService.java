package com.smartattendance.app.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // ===============================
    // STUDENT SIDE (JWT)
    // ===============================

    // 🔐 Student login (JWT issued, device-bound)
    @POST("auth/student/login")
    Call<StudentLoginResponse> studentLogin(
            @Body StudentLoginRequest request
    );

    // 📚 Get active classes
    @GET("classes/active")
    Call<List<ClassSessionModel>> getActiveClasses();

    // 🧠 Mark attendance (JWT identifies student)
    @Multipart
    @POST("attendance/mark")
    Call<ApiResponse> markAttendance(
            @Part("classId") RequestBody classId,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part MultipartBody.Part selfie
    );

    // ===============================
    // TEACHER SIDE (JWT)
    // ===============================

    // 🔐 Teacher login
    @POST("api/teacher/login")
    Call<TeacherLoginResponse> teacherLogin(
            @Body TeacherLoginRequest request
    );

    // 🏫 Create class (JWT identifies teacher)
    @POST("api/teacher/create-class")
    Call<ApiResponse> createClass(
            @Body CreateClassRequest request
    );

    // 📋 Load teacher classes (JWT-based)
    @GET("api/teacher/classes")
    Call<List<ClassSessionModel>> getTeacherClasses();
}
