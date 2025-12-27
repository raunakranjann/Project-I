package com.smartattendance.app.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        SharedPreferences prefs =
                context.getSharedPreferences("auth", Context.MODE_PRIVATE);

        String token = prefs.getString("jwt", null);

        Request original = chain.request();

        if (token != null && !token.isBlank()) {
            Request request = original.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        }

        return chain.proceed(original);
    }
}
