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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_otp = findViewById(R.id.btn_verify);
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et_otp = findViewById(R.id.et_otp);
                Map<String,String> verifyRequest = new HashMap<>();
                verifyRequest.put("otp",et_otp.getText().toString());

                apiService = RetrofitClient.getInstance().create(ApiService.class);
                optCheck(verifyRequest);
            }
        });

    }

    public void optCheck(Map<String, String> otpRequest) {
        // Lấy cookies đã lưu cho request đến /api/otp
        List<Cookie> cookies = cookieJar.loadForRequest(HttpUrl.parse("http://192.168.1.6:8081/api/otp"));

        // Nếu có cookies, ta có thể thêm chúng vào request
        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                Log.d("Cookie", "Sending cookie to /api/otp: " + cookie);
            }
        }

        // Tiến hành gửi request tới /api/otp với cookies
        apiService.otpCheck(otpRequest).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int result = response.body();
                    if (result == 1) {
                        Intent intent = new Intent();
                        intent.setClass(OTPActivity.this, resetPassActivity.class);
                        startActivity(intent);
                    } else if (result == 0) {
                        Toast.makeText(OTPActivity.this, "Sai OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OTPActivity.this, "OTP không hợp lệ chọn gửi lại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}