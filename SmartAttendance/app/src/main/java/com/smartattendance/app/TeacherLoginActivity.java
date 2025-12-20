package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.smartattendance.app.network.RetrofitClient;
import com.smartattendance.app.network.TeacherLoginRequest;
import com.smartattendance.app.network.TeacherLoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginBtn;
    private ProgressBar loader;
    private CheckBox rememberMeCheck;

    private SharedPreferences prefs;

    private static final String TAG = "TeacherLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);   // ✅ FIXED
        loader = findViewById(R.id.loader);
        rememberMeCheck = findViewById(R.id.rememberMeCheck);

        prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);

        /* ---------- AUTO LOGIN (ONLY ON COLD START) ---------- */
        if (savedInstanceState == null) {
            boolean isLoggedIn = prefs.getBoolean("teacher_logged_in", false);
            if (isLoggedIn) {
                Intent i = new Intent(this, TeacherDashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return;
            }
        }

        loginBtn.setOnClickListener(v -> login());
    }

    private void login() {

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        loader.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        TeacherLoginRequest request =
                new TeacherLoginRequest(username, password);

        RetrofitClient.getApiService()
                .teacherLogin(request)
                .enqueue(new Callback<TeacherLoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<TeacherLoginResponse> call,
                            Response<TeacherLoginResponse> response) {

                        loader.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(
                                    TeacherLoginActivity.this,
                                    "Server error",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        TeacherLoginResponse res = response.body();

                        Log.d(TAG,
                                "Login response -> teacherId="
                                        + res.getTeacherId()
                                        + ", message="
                                        + res.getMessage()
                        );

                        // ✅ SAME SUCCESS CHECK AS STUDENT LOGIN
                        if (res.getTeacherId() != null && res.getTeacherId() > 0) {

                            if (rememberMeCheck.isChecked()) {
                                prefs.edit()
                                        .putBoolean("teacher_logged_in", true)
                                        .putLong("teacher_id", res.getTeacherId())
                                        .apply();
                            }

                            Toast.makeText(
                                    TeacherLoginActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    TeacherLoginActivity.this,
                                    TeacherDashboardActivity.class
                            );
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );
                            intent.putExtra("teacherId", res.getTeacherId());

                            startActivity(intent);

                        } else {
                            Toast.makeText(
                                    TeacherLoginActivity.this,
                                    res.getMessage() != null
                                            ? res.getMessage()
                                            : "Invalid username or password",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TeacherLoginResponse> call, Throwable t) {
                        loader.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        Log.e(TAG, "Network error", t);

                        Toast.makeText(
                                TeacherLoginActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
