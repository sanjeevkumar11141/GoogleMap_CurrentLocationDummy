package com.example.googlemapdummysk.retrofit;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIServiceInterface {
   // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=YOUR_API_KEY
    @GET("place/nearbysearch/json?")
    Call<JsonElement> doPlaces(@Query(value = "location", encoded = true) String location,
                               @Query(value = "radius", encoded = true) String radius,
                               @Query(value = "type", encoded = true) String type,
                               @Query(value = "key", encoded = true) String key);


}