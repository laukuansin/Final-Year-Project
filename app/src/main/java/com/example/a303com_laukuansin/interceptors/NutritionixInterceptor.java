package com.example.a303com_laukuansin.interceptors;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NutritionixInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("x-app-id","7375706d")
                .addHeader("x-app-key","be8864f4960beb021d2e28b55e4b9353")
                .build();
        return chain.proceed(request);
    }
}

