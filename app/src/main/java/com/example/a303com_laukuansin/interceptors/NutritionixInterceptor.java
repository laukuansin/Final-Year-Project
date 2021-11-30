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
                //below both is empty due to the abuse of the API
                .addHeader("x-app-id","")
                .addHeader("x-app-key","")
                .build();
        return chain.proceed(request);
    }
}

