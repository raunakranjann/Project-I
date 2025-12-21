package com.smartattendance.app.network;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ===============================
    // STUDENT SIDE (JWT)
    // ===============================

    @POST("auth/student/login")
    Call<StudentLoginResponse> studentLogin(
            @Body StudentLoginRequest request
    );

    @GET("classes/active")
    Call<List<ClassSessionModel>> getActiveClasses();

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

    @POST("api/teacher/login")
    Call<TeacherLoginResponse> teacherLogin(
            @Body TeacherLoginRequest request
    );

    @POST("api/teacher/create-class")
    Call<ApiResponse> createClass(
            @Body CreateClassRequest request,
            @Header("Authorization") String token
    );

    @GET("api/teacher/classes")
    Call<List<ClassSessionModel>> getTeacherClasses(
            @Header("Authorization") String token
    );

    @DELETE("api/teacher/classes/{classId}")
    Call<Map<String, String>> deleteClass(
            @Path("classId") long classId,
            @Header("Authorization") String token
    );
}
