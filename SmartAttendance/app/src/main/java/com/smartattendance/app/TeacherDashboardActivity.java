package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ClassSessionModel;
import com.smartattendance.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loader;
    private TextView emptyText;

    private Button btnCreateClass;
    private ImageButton btnLogout;

    private String authToken; // 🔐 JWT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        loader = findViewById(R.id.loader);
        emptyText = findViewById(R.id.emptyText);
        btnCreateClass = findViewById(R.id.btnCreateClass);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // ---------- JWT SESSION CHECK ----------
        SharedPreferences prefs =
                getSharedPreferences("login_prefs", MODE_PRIVATE);

        authToken = prefs.getString("auth_token", null);
        String role = prefs.getString("role", null);

        if (authToken == null || !"TEACHER".equals(role)) {
            Toast.makeText(
                    this,
                    "Session expired. Login again.",
                    Toast.LENGTH_LONG
            ).show();
            redirectToLogin();
            return;
        }

        // ---------- CREATE CLASS ----------
        btnCreateClass.setOnClickListener(v ->
                startActivity(new Intent(
                        TeacherDashboardActivity.this,
                        CreateClassActivity.class
                ))
        );

        // ---------- LOGOUT ----------
        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeacherClasses(); // refresh after create / delete
    }

    private void loadTeacherClasses() {

        loader.setVisibility(ProgressBar.VISIBLE);
        recyclerView.setVisibility(RecyclerView.GONE);
        emptyText.setVisibility(TextView.GONE);

        RetrofitClient.getApiService(this)
                .getTeacherClasses("Bearer " + authToken)
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
                            emptyText.setText("No classes created yet");
                            emptyText.setVisibility(TextView.VISIBLE);
                            return;
                        }

                        recyclerView.setAdapter(
                                new TeacherClassAdapter(classes, authToken)
                        );
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
        SharedPreferences prefs =
                getSharedPreferences("login_prefs", MODE_PRIVATE);
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
