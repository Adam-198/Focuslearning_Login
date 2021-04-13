package com.scut.focuslearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.scut.focuslearning.Utils.PhoneUtil;
import com.scut.focuslearning.Utils.ServerUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.FormBody;

public class RegisterActivity extends AppCompatActivity {
    private Button button_register;
    private EditText edit_username;
    private RadioButton girlRadio = null;
    private RadioButton boyRadio = null;
    private EventHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button_register = findViewById(R.id.button_Register_register);
        edit_username = findViewById(R.id.edit_Register_username);
        girlRadio = (RadioButton) this.findViewById(R.id.radio_girl);
        boyRadio = (RadioButton) this.findViewById(R.id.radio_boy);



        handler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                PhoneUtil.callbackVerificationCode(RegisterActivity.this, event, result, data);
            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(handler);


        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edit_username.getText().toString();

                if(username.isEmpty()) {

                } else {
                    PhoneUtil.submitVerificationCode(getApplicationContext(),phoneNumber,code);
                    FormBody body = new FormBody.Builder()
                            .add("username",username)
                            .add("password",password)
                            .add("phonenumber",phoneNumber)
                            .build();
                    int StatusCode = ServerUtil.RequestSever("register",body);
                    if(StatusCode==200) {
                        Intent intent = new Intent(RegisterActivity.this, CountdownActivity.class);
                        startActivity(intent);
                        System.out.println("注册成功！");
                    }
                }

            }
        });
    }
}