package com.smartattendance.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.smartattendance.app.network.ApiService;
import com.smartattendance.app.network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText rollNo, password;
    private Button loginBtn, facultyLoginBtn;
    private CheckBox rememberMeCheck;

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
        rememberMeCheck = findViewById(R.id.rememberMeCheck);

        prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        apiService = RetrofitClient.getApiService();

        /* ---------- AUTO LOGIN (ONLY ON COLD START) ---------- */
        if (savedInstanceState == null) {

            boolean teacherLoggedIn = prefs.getBoolean("teacher_logged_in", false);
            boolean studentLoggedIn = prefs.getBoolean("is_logged_in", false);

            if (teacherLoggedIn) {
                Intent i = new Intent(this, TeacherDashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return;
            }

            if (studentLoggedIn) {
                Intent i = new Intent(this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return;
            }
        }


        /* ---------- STUDENT LOGIN ---------- */
        loginBtn.setOnClickListener(v -> {

            String roll = rollNo.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (roll.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String deviceId = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );

            apiService.login(roll, pass, deviceId)
                    .enqueue(new Callback<Map<String, String>>() {

                        @Override
                        public void onResponse(Call<Map<String, String>> call,
                                               Response<Map<String, String>> response) {

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && "SUCCESS".equals(response.body().get("status"))) {

                                if (rememberMeCheck.isChecked()) {
                                    prefs.edit()
                                            .putBoolean("is_logged_in", true)
                                            .putString("roll_no", roll)
                                            .apply();
                                }

                                Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);

                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Invalid credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,
                                    "Network error",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        /* ---------- FACULTY LOGIN ---------- */
        facultyLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherLoginActivity.class));
        });
    }
}
