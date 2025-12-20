package com.smartattendance.app.network;

import com.google.gson.annotations.SerializedName;

public class ClassSessionModel {

    // ---------- BASIC ----------
    private long id;

    @SerializedName("subjectName")
    private String subjectName;

    // ---------- TEACHER ----------
    // IMPORTANT:
    // Backend MUST send either "teacherName" OR "teacher_name"
    @SerializedName(value = "teacherName", alternate = {"teacher_name"})
    private String teacherName;

    // ---------- LOCATION ----------
    private double latitude;
    private double longitude;
    private double radius;

    // ---------- TIME ----------
    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    // ---------- GETTERS ----------

    public long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
