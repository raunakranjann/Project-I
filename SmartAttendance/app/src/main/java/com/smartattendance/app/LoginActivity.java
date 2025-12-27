package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.smartattendance.app.network.ApiService;
import com.smartattendance.app.network.RetrofitClient;
import com.smartattendance.app.network.StudentLoginRequest;
import com.smartattendance.app.network.StudentLoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText rollNo, password;
    private Button loginBtn, facultyLoginBtn;

    private SharedPreferences prefs;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        rollNo = findViewById(R.id.rollNo);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        facultyLoginBtn = findViewById(R.id.facultyLoginBtn);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        apiService = RetrofitClient.getApiService(this);

        /* ---------- AUTO LOGIN USING JWT ---------- */
        if (savedInstanceState == null) {
            String token = prefs.getString("jwt", null);
            String role  = prefs.getString("role", null);

            if (token != null && role != null) {

                Intent i = "TEACHER".equals(role)
                        ? new Intent(this, TeacherDashboardActivity.class)
                        : new Intent(this, DashboardActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return;
            }
        }

        /* ---------- STUDENT LOGIN ---------- */
        loginBtn.setOnClickListener(v -> {

            String roll = rollNo.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (roll.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,
                        "Enter all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String deviceId = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );

            StudentLoginRequest request =
                    new StudentLoginRequest(roll, pass, deviceId);

            apiService.studentLogin(request)
                    .enqueue(new Callback<StudentLoginResponse>() {

                        @Override
                        public void onResponse(
                                Call<StudentLoginResponse> call,
                                Response<StudentLoginResponse> response) {

                            if (response.body() != null && response.body().isSuccess()) {


                                StudentLoginResponse res = response.body();

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();

                                editor.putString("jwt", res.getToken());
                                editor.putString("role", "STUDENT");
                                editor.apply();

                                Intent i = new Intent(
                                        LoginActivity.this,
                                        DashboardActivity.class
                                );
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else {
                                Toast.makeText(
                                        LoginActivity.this,
                                        response.body() != null
                                                ? response.body().getMessage()
                                                : "Login failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }

                        @Override
                        public void onFailure(
                                Call<StudentLoginResponse> call,
                                Throwable t) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Network error",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        /* ---------- FACULTY LOGIN ---------- */
        facultyLoginBtn.setOnClickListener(v ->
                startActivity(
                        new Intent(this, TeacherLoginActivity.class)
                )
        );
    }
}
