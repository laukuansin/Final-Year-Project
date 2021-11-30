package com.example.a303com_laukuansin.interceptors;

import android.util.Base64;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IBMWatsonInterceptor implements Interceptor{
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String username="apikey";
        //String password=""; Here is empty because due to the abuse of the API
        String auth=username+":"+password;
        String authHeader="Basic "+ Base64.encodeToString(auth.getBytes(),Base64.NO_WRAP);

        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization",authHeader)
                .build();
        return chain.proceed(request);
    }
}

