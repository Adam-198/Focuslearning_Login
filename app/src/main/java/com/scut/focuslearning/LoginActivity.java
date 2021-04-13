package com.scut.focuslearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scut.focuslearning.Utils.ServerUtil;

import okhttp3.FormBody;

public class LoginActivity extends AppCompatActivity {
    private Button button_login, button_register, button_bycode;
    private EditText edit_username;
    private EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button_login = findViewById(R.id.button_Login_login);
        button_register = findViewById(R.id.button_Login_register);
        button_bycode = findViewById(R.id.button_Login_ByCode);
        edit_username = findViewById(R.id.edit_Login_username);
        edit_password = findViewById(R.id.edit_Login_password);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String username = edit_username.getText().toString();
//                String password = edit_password.getText().toString();
//                Login(username, password);

                Intent intent = new Intent(LoginActivity.this, CountdownActivity.class);
                startActivity(intent);
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String username = edit_username.getText().toString();
//                String password = edit_password.getText().toString();
//                register(username,password);
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        button_bycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginByPhoneActivity.class);
                startActivity(intent);
            }
        });

    }


    private void Login(String username, String password) {
        FormBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        int StatusCode = ServerUtil.RequestSever("login",body);
        if(StatusCode==200) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            System.out.println("登录成功！");
        }
    }

}