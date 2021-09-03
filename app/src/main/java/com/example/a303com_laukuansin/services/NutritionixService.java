package com.example.a303com_laukuansin.services;

import com.example.a303com_laukuansin.domains.FoodDetail;
import com.example.a303com_laukuansin.requests.MealDetailRequest;
import com.example.a303com_laukuansin.responses.MealDetailResponse;
import com.example.a303com_laukuansin.responses.MealResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NutritionixService {
    @GET("search/instant")
    Call<MealResponse> searchFoodWithQuery(@Query("query") String query);

    @POST("natural/nutrients")
    Call<MealDetailResponse> getCommonFoodDetail(@Body MealDetailRequest request);

    @GET("search/item")
    Call<MealDetailResponse> getBrandedFoodDetail(@Query("nix_item_id") String itemID);

    @GET("search/item")
    Call<MealDetailResponse> getBarcodeFoodDetail(@Query("upc") String barcode);

}
