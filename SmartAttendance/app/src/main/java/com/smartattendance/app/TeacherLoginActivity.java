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
        loginBtn = findViewById(R.id.loginBtn);
        loader = findViewById(R.id.loader);
        rememberMeCheck = findViewById(R.id.rememberMeCheck);

        prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);

        // ---------- AUTO LOGIN (JWT + ROLE) ----------
        if (savedInstanceState == null) {
            String token = prefs.getString("auth_token", null);
            String role  = prefs.getString("role", null);

            if (token != null && "TEACHER".equals(role)) {
                Intent i = new Intent(this, TeacherDashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return;
            }
        }

        loginBtn.setOnClickListener(v -> login());
    }

    private void login() {

        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Enter username and password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        loader.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        TeacherLoginRequest request =
                new TeacherLoginRequest(username, password);

        RetrofitClient.getApiService(this)
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
                                "Login response -> success="
                                        + res.isSuccess()
                                        + ", token="
                                        + res.getToken());

                        if (res.isSuccess() && res.getToken() != null) {

                            SharedPreferences.Editor editor = prefs.edit();

                            // 🔐 ALWAYS SAVE JWT
                            editor.putString("auth_token", res.getToken());
                            editor.putString("role", "TEACHER");

                            // 🔁 Remember-me only controls auto-login
                            editor.putBoolean(
                                    "remember_me",
                                    rememberMeCheck.isChecked()
                            );

                            editor.apply();

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
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(
                                    TeacherLoginActivity.this,
                                    res.getMessage() != null
                                            ? res.getMessage()
                                            : "Invalid credentials",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<TeacherLoginResponse> call,
                            Throwable t) {

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
