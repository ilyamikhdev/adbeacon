package com.adbeacon.api;

import com.adbeacon.model.BeaconText;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiService {
    @GET("/")
    void getBeaconPhrase(@Query("api_key") String apiKey,
                         @Query("method") String method,
                         @Query("token") String token,
                         @Query("deviceId") String deviceId,
                         @Query("userId") int userId,
                         @Query(encodeValue = false, value = "arrayBeacon[]") ArrayList<String> arrayBeacon,
                         Callback<BeaconText> callback);
}
