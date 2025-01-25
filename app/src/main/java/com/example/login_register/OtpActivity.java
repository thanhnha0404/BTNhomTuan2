package com.example.login_register;

import static com.example.login_register.AuthService.authService;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private EditText editTextOtp;
    private Button buttonVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        editTextOtp = findViewById(R.id.editTextOtp);
        buttonVerify = findViewById(R.id.buttonVerify);
        String email = getIntent().getStringExtra("email");

        buttonVerify.setOnClickListener(view -> {
            String otp = editTextOtp.getText().toString();

            if (!otp.isEmpty()) {
                verifyOtp(otp,email);
            } else {
                Toast.makeText(OtpActivity.this, "Vui lòng nhập OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtp(String otp, String email) {
        // Tạo Map chứa OTP để gửi qua API

        Map<String, String> otpMap = new HashMap<>();
        otpMap.put("email", email);
        otpMap.put("otp", otp);

        // Gọi API xác thực OTP
        authService.verifyOtp(otpMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Chuyển hướng hoặc thông báo thành công
                    Toast.makeText(OtpActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình chính hoặc đăng nhập
                } else {
                    Toast.makeText(OtpActivity.this, "OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

