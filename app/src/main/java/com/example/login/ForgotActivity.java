package com.example.login;

import static com.example.login.api.RetrofitClient.cookieJar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.login.api.ApiService;
import com.example.login.api.RetrofitClient;
import com.example.login.entity.ErrorResponseDTO;
import com.example.login.entity.userDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.btn_reset);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_name = findViewById(R.id.et_name);

                Map<String,String> forgotRequest = new HashMap<>();
                forgotRequest.put("name",et_name.getText().toString());

                apiService = RetrofitClient.getInstance().create(ApiService.class);
                forgot(forgotRequest);
            }
        });

    }

    private void forgot(Map<String,String> forgotRequest){

        apiService.forgot(forgotRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {
                    // Phản hồi thành công
                    List<Cookie> cookies = Cookie.parseAll(HttpUrl.parse("http://192.168.1.6:8081/api/forgot"), response.headers());
                    if (cookies != null && !cookies.isEmpty()) {
                        cookieJar.saveFromResponse(HttpUrl.parse("http://192.168.1.6:8081/api/otp"), cookies);
                    }



                    Intent intent = new Intent();
                    intent.setClass(ForgotActivity.this,OTPActivity.class);
                    startActivity(intent);

                    // Khởi chạy Intent với ứng dụng hỗ trợ

                } else {
                    // Nếu phản hồi không thành công, đọc từ errorBody
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                        // Hiển thị lỗi từ server
                        Toast.makeText(ForgotActivity.this, error.getError(), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ForgotActivity.this, "Lỗi khi đọc phản hồi lỗi từ server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(ForgotActivity.this, "Lỗi kết nối: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
