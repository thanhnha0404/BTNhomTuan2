package com.example.login.api;

import com.example.login.entity.userDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.GsonBuildConfig;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/login")
    Call<Object> login(@Body Map<String,String> loginRequest);


    @POST("/api/forgot")
    Call<Object> forgot(@Body Map<String,String> forgotRequest);

    @POST("/api/otp")
    Call<Integer> otpCheck (@Body Map<String,String> verifyRequest);

    @POST("/api/reset")
    Call<Integer> resetPass (@Body Map<String,String> resetRequest);
    

}
