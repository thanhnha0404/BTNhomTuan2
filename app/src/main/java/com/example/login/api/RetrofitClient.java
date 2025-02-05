package com.example.login.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    // CookieJar để lưu cookie giữa các request
    public static CookieJar cookieJar = new CookieJar() {
        private final Map<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url, cookies);
            // Lưu cookies cho các URL khác có cùng domain
            HttpUrl baseUrl = url.newBuilder().host(url.host()).build();
            cookieStore.put(baseUrl, cookies);

            for (Cookie cookie : cookies) {
                System.out.println("Saved Cookie: " + cookie);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            // Trả về cookies cho URL này hoặc cho các URL có cùng domain
            List<Cookie> cookies = cookieStore.get(url);
            if (cookies == null) {
                // Lấy cookies từ URL có cùng domain nếu không tìm thấy cookies cho URL này
                HttpUrl baseUrl = url.newBuilder().host(url.host()).build();
                cookies = cookieStore.get(baseUrl);
            }

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println("Loaded Cookie for Request: " + cookie);
                }
            }

            return cookies != null ? cookies : new ArrayList<>();
        }
    };

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Tích hợp CookieJar vào OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS) // Thời gian kết nối tối đa
                    .readTimeout(120, TimeUnit.SECONDS)    // Thời gian đọc dữ liệu tối đa
                    .writeTimeout(120, TimeUnit.SECONDS)   // Thời gian ghi dữ liệu tối đa
                    .cookieJar(cookieJar)  // Thêm CookieJar vào OkHttpClient để quản lý cookies
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))  // Hiển thị headers và body khi debug
                    .build();

            // Cấu hình Retrofit với OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.6:8081/") // Địa chỉ server
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()) // Sử dụng Gson để chuyển đổi JSON
                    .build();
        }
        return retrofit;
    }
}
