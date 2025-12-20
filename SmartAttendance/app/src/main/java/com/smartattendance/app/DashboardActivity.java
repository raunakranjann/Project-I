package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ClassSessionModel;
import com.smartattendance.app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loader;
    private TextView emptyText;
    private ImageButton logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        loader = findViewById(R.id.loader);
        emptyText = findViewById(R.id.emptyText);
        logoutBtn = findViewById(R.id.logoutBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences prefs =
                    getSharedPreferences("login_prefs", MODE_PRIVATE);

            prefs.edit().clear().apply();

            Intent intent = new Intent(
                    DashboardActivity.this,
                    LoginActivity.class
            );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveClasses();
    }

    private void loadActiveClasses() {
        loader.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);

        RetrofitClient.getApiService(this)
                .getActiveClasses()
                .enqueue(new Callback<List<ClassSessionModel>>() {

                    @Override
                    public void onResponse(
                            Call<List<ClassSessionModel>> call,
                            Response<List<ClassSessionModel>> response) {

                        loader.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {
                            emptyText.setText("Failed to load classes");
                            emptyText.setVisibility(View.VISIBLE);
                            return;
                        }

                        List<ClassSessionModel> classes = response.body();

                        if (classes.isEmpty()) {
                            emptyText.setText("No active classes right now");
                            emptyText.setVisibility(View.VISIBLE);
                            return;
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(
                                new StudentClassAdapter(
                                        classes,
                                        DashboardActivity.this
                                )
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<List<ClassSessionModel>> call,
                            Throwable t) {

                        loader.setVisibility(View.GONE);
                        emptyText.setText("Network error");
                        emptyText.setVisibility(View.VISIBLE);
                    }
                });
    }
}
