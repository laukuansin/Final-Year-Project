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
        String password="FckSwGh6_xRcXUINH_7f0RnJAjiR5D_WIpbuUuLng-va";
        String auth=username+":"+password;
        String authHeader="Basic "+ Base64.encodeToString(auth.getBytes(),Base64.NO_WRAP);

        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization",authHeader)
                .build();
        return chain.proceed(request);
    }
}

