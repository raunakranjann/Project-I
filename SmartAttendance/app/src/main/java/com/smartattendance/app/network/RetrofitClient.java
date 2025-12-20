package com.smartattendance.app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ⚠️ IMPORTANT:
    // Use PC IP when testing on real phone
    // Use 10.0.2.2 if testing on emulator
    private static final String BASE_URL = "http://192.168.137.1:8080/";

    private static Retrofit retrofit;

    private RetrofitClient() {
        // Prevent instantiation
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
