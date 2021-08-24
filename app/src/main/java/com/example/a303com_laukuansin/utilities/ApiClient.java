package com.example.a303com_laukuansin.utilities;

import com.example.a303com_laukuansin.interceptors.NutritionixInterceptor;
import com.example.a303com_laukuansin.services.NutritionixService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/*
    For purpose connect API
*/
public class ApiClient {
    private static Retrofit getRetrofit(String url, Interceptor interceptor)
    {
        HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient=new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).addInterceptor(interceptor).build();


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public static NutritionixService getNutritionixService()
    {
        NutritionixService nutritionixService = getRetrofit("https://trackapi.nutritionix.com/v2/",new NutritionixInterceptor()).create(NutritionixService.class);
        return nutritionixService;
    }
}
