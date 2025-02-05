package com.example.login;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class resetPassActivity extends AppCompatActivity {

    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button resetButton = findViewById(R.id.btn_reset);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText pass1 = findViewById(R.id.et_password_1);
                EditText pass2 = findViewById(R.id.et_password_2);

                Map<String,String> resetRequest = new HashMap<>();
                resetRequest.put("pass1",pass1.getText().toString());
                resetRequest.put("pass2",pass2.getText().toString());

                apiService = RetrofitClient.getInstance().create(ApiService.class);

                resetPass(resetRequest);

            }
        });
    }

    public void resetPass( Map<String,String> resetRequest){
        apiService.resetPass(resetRequest).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null){
                    int result = response.body();
                    if (result == 1){

                        Toast.makeText(resetPassActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(resetPassActivity.this,MainActivity.class);
                    }
                    else if (result == 0){
                        Toast.makeText(resetPassActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(resetPassActivity.this, "Vui lòng điền mật khẩu và xác thực", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(resetPassActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}