package com.scut.focuslearning;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scut.focuslearning.Utils.PhoneUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginByPhoneActivity extends Activity {
    private Button button_login, button_code;
    private EditText edit_phoneNumber, edit_code;
    private String phoneNum, code;
    private EventHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginbyphone);
        button_login = findViewById(R.id.button_LoginByPhone_login);
        button_code = findViewById(R.id.button_LoginByPhone_code);
        edit_phoneNumber = findViewById(R.id.edit_LoginByPhone_phonenumber);
        edit_code = findViewById(R.id.edit_LoginByPhone_code);

        handler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                PhoneUtil.callbackVerificationCode(LoginByPhoneActivity.this, event, result, data);
            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(handler);

        button_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = edit_phoneNumber.getText().toString();
                PhoneUtil.getVerificationCode(getApplicationContext(), phoneNum);
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = edit_code.getText().toString();
                PhoneUtil.submitVerificationCode(getApplicationContext(), phoneNum, code);
            }
        });

    }

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(handler);
    }
}
