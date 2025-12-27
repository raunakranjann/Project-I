package com.smartattendance.app.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ---------- BASE URLs ----------
    private static final String EMULATOR_URL = "http://10.0.2.2:8080/";
    private static final String PHONE_URL    = "http://192.168.137.1:8080/";

    private static Retrofit retrofit;

    private RetrofitClient() {
        // no instance
    }

    // ---------- Detect runtime ----------
    private static String getBaseUrl() {
        boolean isEmulator =
                Build.FINGERPRINT.contains("generic")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK");

        return isEmulator ? EMULATOR_URL : PHONE_URL;
    }

    // ---------- API SERVICE ----------
    public static ApiService getApiService(Context context) {

        if (retrofit == null) {

            Context appContext = context.getApplicationContext();
            String baseUrl = getBaseUrl();

            Log.d("RETROFIT", "Base URL = " + baseUrl);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(appContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}
