package com.example.login;

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
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.btn_login);
        Button forgotButton = findViewById(R.id.btn_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_name = findViewById(R.id.et_email);
                EditText et_pass = findViewById(R.id.et_password);

                Map<String,String> loginRequest = new HashMap<>();
                loginRequest.put("name",et_name.getText().toString());
                loginRequest.put("pass",et_pass.getText().toString());

                apiService = RetrofitClient.getInstance().create(ApiService.class);
                login(loginRequest);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,ForgotActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login(Map<String,String> loginRequest){

        apiService.login(loginRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Gson gson = new Gson();
                if (response.isSuccessful() && response.body() != null) {
                    // Phản hồi thành công
                    String json = gson.toJson(response.body());
                    userDTO u = gson.fromJson(json, userDTO.class);
                    if (u.getStatus() == 1) {
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Tài khoản chưa được kích hoạt", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu phản hồi không thành công, đọc từ errorBody
                    try {
                        String errorJson = response.errorBody() != null ? response.errorBody().string() : "";
                        ErrorResponseDTO error = gson.fromJson(errorJson, ErrorResponseDTO.class);
                        // Hiển thị lỗi từ server
                        Toast.makeText(MainActivity.this, error.getError(), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Lỗi khi đọc phản hồi lỗi từ server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}