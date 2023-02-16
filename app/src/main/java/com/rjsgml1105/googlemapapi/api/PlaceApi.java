package com.rjsgml1105.googlemapapi.api;

import com.rjsgml1105.googlemapapi.model.PlaceList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApi {
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceLIST(
            @Query("keyword") String keyword,
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("language") String langage,
            @Query("key") String key,
            @Query("pagetoken") String pagetoken
    );

}
