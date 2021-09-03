package com.example.a303com_laukuansin.services;

import com.example.a303com_laukuansin.responses.FoodClassifyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface IBMWatsonService {
    @GET("classify")
    Call<FoodClassifyResponse> getFoodClassify(@Query("url") String url, @Query("version")String version, @Query("classifier_ids")String label);
}
