package com.scut.focuslearning.Utils;

import android.widget.Toast;

import com.google.gson.Gson;
import com.scut.focuslearning.LoginActivity;
import com.scut.focuslearning.Login_cs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
访问Django服务器
 */
public class ServerUtil {
    private static final String ipAdress = "172.20.10.2";
    private static final int port = 8000;
    private static final String url = "http://" + ipAdress + ":" + port + "/";

    public static int RequestSever(String apiName, FormBody body) {
        OkHttpClient client = new OkHttpClient();
        String requestUrl = url + apiName + "/";
        final Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();
        final int[] StatusCode = {200};
        Object object =new Object();
        synchronized(object) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e);
                    System.out.println(call);
                    StatusCode[0] = 404;
                    synchronized (object) {
                        object.notify();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String date = response.body().string();
                    Gson gson = new Gson();
                    Login_cs login_cs = gson.fromJson(date, Login_cs.class);
                    StatusCode[0] = login_cs.get_$StatusCode185();
                    synchronized (object) {
                        object.notify();
                    }
                }
            });
            try {
                object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return StatusCode[0];
    }
}
