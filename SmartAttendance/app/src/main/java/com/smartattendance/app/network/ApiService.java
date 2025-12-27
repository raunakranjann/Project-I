package com.smartattendance.app.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import retrofit2.http.GET;
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

    // 🔒 TODAY ONLY (backend enforced)
    @GET("api/teacher/classes/today")
    Call<List<ClassSessionModel>> getTeacherTodayClasses();

    // 🔒 Students for LIVE class only
    @GET("api/teacher/classes/{classId}/students")
    Call<ApiResponse<List<StudentModel>>> getStudentsForClass(
            @Path("classId") Long classId
    );

    // 🔒 Manual attendance (one-time)
    @FormUrlEncoded
    @POST("api/teacher/attendance/mark")
    Call<ApiResponse<String>> markAttendanceByTeacher(
            @Field("classId") Long classId,
            @Field("studentId") Long studentId,
            @Field("status") String status   // PRESENT / ABSENT
    );
}
