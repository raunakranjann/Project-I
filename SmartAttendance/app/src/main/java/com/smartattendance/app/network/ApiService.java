package com.smartattendance.app.network;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    // ===============================
    // STUDENT SIDE
    // ===============================

    @GET("classes/active")
    Call<List<ClassSessionModel>> getActiveClasses();

    @POST("auth/login")
    Call<Map<String, String>> login(
            @Query("rollNo") String rollNo,
            @Query("password") String password,
            @Query("deviceId") String deviceId
    );

    @Multipart
    @POST("attendance/mark")
    Call<ApiResponse> markAttendance(
            @Part("userId") RequestBody userId,
            @Part("classId") RequestBody classId,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part MultipartBody.Part selfie
    );

    // ===============================
    // TEACHER SIDE
    // ===============================

    @POST("api/teacher/login")
    Call<TeacherLoginResponse> teacherLogin(
            @Body TeacherLoginRequest request
    );







    @POST("/api/teacher/create-class")
    Call<ApiResponse> createClass(
            @Body CreateClassRequest request
    );

    @GET("/api/teacher/classes")
    Call<List<ClassSessionModel>> getTeacherClasses(
            @Query("teacherId") Long teacherId
    );



}
