package com.scut.focuslearning.Utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.SMSSDK;

public class PhoneUtil {
    /**
     * 正则匹配手机号码:
     */
    public static boolean checkTel(String phoneNumber){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$");
        Matcher matcher = p.matcher(phoneNumber);
        return matcher.matches();
    }

    public static void getVerificationCode(Context context, String phoneNumber) {
        if(!phoneNumber.isEmpty()){
            if(checkTel(phoneNumber)){ //利用正则表达式获取检验手机号
                // 获取验证码
                SMSSDK.getVerificationCode("86", phoneNumber);
            }else{
                Toast.makeText(context,"请输入有效的手机号",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(context,"请输入手机号",Toast.LENGTH_LONG).show();
            return;
        }
    }

    public static void submitVerificationCode(Context context,String phoneNumber,String code) {
        if(!code.isEmpty()){
            //提交验证码
            SMSSDK.submitVerificationCode("86", phoneNumber, code);
        }else{
            Toast.makeText(context,"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }
    }

    public static void callbackVerificationCode(Activity activity,int event, int result, Object data) {
        if (result == SMSSDK.RESULT_COMPLETE){
            //回调完成
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"登录成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                //获取验证码成功
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"验证码已发送",Toast.LENGTH_SHORT).show();
                    }
                });
            }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                Log.i("test","test");
            }
        }else{
            //其他失败情况（验证码校验失败等）
            ((Throwable)data).printStackTrace();
            Throwable throwable = (Throwable) data;
            throwable.printStackTrace();
            Log.i("1234",throwable.toString());
            try {
                JSONObject obj = new JSONObject(throwable.getMessage());
                final String des = obj.optString("detail");
                if (!TextUtils.isEmpty(des)){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,des,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
