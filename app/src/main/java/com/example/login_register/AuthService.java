package com.example.login_register;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // 10.0.2.2 là localhost trên Android Emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    AuthService authService = retrofit.create(AuthService.class);
    @POST("auth/register")
    Call<ResponseBody> register(@Body Map<String, String> body);

    @POST("auth/verify-otp")
    Call<ResponseBody> verifyOtp(@Body Map<String, String> body);
}
