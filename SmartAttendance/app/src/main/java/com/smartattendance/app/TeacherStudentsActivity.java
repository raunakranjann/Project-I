package com.smartattendance.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ApiResponse;
import com.smartattendance.app.network.RetrofitClient;
import com.smartattendance.app.network.StudentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherStudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loader;
    private long classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_students);

        recyclerView = findViewById(R.id.recyclerView);
        loader = findViewById(R.id.loader);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // ===============================
        // READ CLASS ID
        // ===============================
        classId = getIntent().getLongExtra("classId", -1);
        if (classId <= 0) {
            Toast.makeText(this, "Invalid class", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudents();
    }

    // ===============================
    // LOAD STUDENTS FOR CLASS
    // ===============================
    private void loadStudents() {

        loader.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        RetrofitClient.getApiService(this)
                .getStudentsForClass(classId)
                .enqueue(new Callback<ApiResponse<List<StudentModel>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<StudentModel>>> call,
                            Response<ApiResponse<List<StudentModel>>> response) {

                        loader.setVisibility(View.GONE);

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().isSuccess()) {

                            Toast.makeText(
                                    TeacherStudentsActivity.this,
                                    "Failed to load students",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        List<StudentModel> students = response.body().getData();

                        if (students == null || students.isEmpty()) {
                            Toast.makeText(
                                    TeacherStudentsActivity.this,
                                    "No students found",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        // ✅ FINAL ADAPTER (TOGGLE READY)
                        TeacherStudentAdapter adapter =
                                new TeacherStudentAdapter(
                                        TeacherStudentsActivity.this,
                                        students,
                                        classId
                                );

                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<StudentModel>>> call,
                            Throwable t) {

                        loader.setVisibility(View.GONE);
                        Toast.makeText(
                                TeacherStudentsActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
