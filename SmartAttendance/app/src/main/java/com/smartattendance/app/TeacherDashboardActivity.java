package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ClassSessionModel;
import com.smartattendance.app.network.RetrofitClient;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loader;
    private TextView emptyText;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        loader = findViewById(R.id.loader);
        emptyText = findViewById(R.id.emptyText);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // ---------- JWT SESSION CHECK ----------
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt", null);
        String role  = prefs.getString("role", null);

        if (token == null || !"TEACHER".equals(role)) {
            Toast.makeText(this,
                    "Session expired. Login again.",
                    Toast.LENGTH_LONG).show();
            redirectToLogin();
            return;
        }

        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayClasses();
    }

    private void loadTodayClasses() {

        loader.setVisibility(ProgressBar.VISIBLE);
        recyclerView.setVisibility(RecyclerView.GONE);
        emptyText.setVisibility(TextView.GONE);

        RetrofitClient.getApiService(this)
                .getTeacherTodayClasses()
                .enqueue(new Callback<List<ClassSessionModel>>() {

                    @Override
                    public void onResponse(
                            Call<List<ClassSessionModel>> call,
                            Response<List<ClassSessionModel>> response) {

                        loader.setVisibility(ProgressBar.GONE);

                        if (!response.isSuccessful() || response.body() == null) {
                            emptyText.setText("Failed to load classes");
                            emptyText.setVisibility(TextView.VISIBLE);
                            return;
                        }

                        List<ClassSessionModel> classes = response.body();

                        if (classes.isEmpty()) {
                            emptyText.setText("No classes scheduled today");
                            emptyText.setVisibility(TextView.VISIBLE);
                            return;
                        }

                        // ✅ SAFE CLICK HANDLING
                        TeacherClassAdapter adapter =
                                new TeacherClassAdapter(classes, session -> {

                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime start =
                                            LocalDateTime.parse(session.getStartTime());
                                    LocalDateTime end =
                                            LocalDateTime.parse(session.getEndTime());

                                    if (now.isBefore(start)) {
                                        Toast.makeText(
                                                TeacherDashboardActivity.this,
                                                "Class has not started yet",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    if (now.isAfter(end)) {
                                        Toast.makeText(
                                                TeacherDashboardActivity.this,
                                                "Class already ended",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    // ✅ LIVE CLASS → OPEN STUDENTS
                                    Intent intent = new Intent(
                                            TeacherDashboardActivity.this,
                                            TeacherStudentsActivity.class
                                    );

                                    intent.putExtra("classId", session.getId());
                                    intent.putExtra("subject", session.getSubjectName());
                                    intent.putExtra("startTime", session.getStartTime());
                                    intent.putExtra("endTime", session.getEndTime());

                                    startActivity(intent);
                                });

                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(RecyclerView.VISIBLE);
                    }

                    @Override
                    public void onFailure(
                            Call<List<ClassSessionModel>> call,
                            Throwable t) {

                        loader.setVisibility(ProgressBar.GONE);
                        emptyText.setText("Network error");
                        emptyText.setVisibility(TextView.VISIBLE);
                    }
                });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
