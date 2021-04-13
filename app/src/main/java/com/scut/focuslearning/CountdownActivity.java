package com.scut.focuslearning;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.scut.focuslearning.Utils.BackgroundUtil;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownActivity extends AppCompatActivity {
    private Button button_Countdown_start, button_Countdown_stop;
    private static CountdownView countdownView;
    private int time = 900;
    private int minute = 15;
    private final Timer[] timer = {new Timer()};
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        button_Countdown_start = findViewById(R.id.button_Countdown_start);
        button_Countdown_stop = findViewById(R.id.button_Countdown_stop);
        countdownView = findViewById(R.id.view_countdown);
        // 设置倒计时时长
        countdownView.setCountdown(time);
        // 设置倒计时改变监听
        countdownView.setOnCountdownListener(new CountdownView.OnCountdownListener() {
            @Override
            public void countdown(int minute) {
                CountdownActivity.this.minute = minute;
                CountdownActivity.this.time = 60 * minute;
            }
        });
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            //若未授权则请求权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }

        // 开始倒计时监听
        button_Countdown_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer[0].cancel();
                timer[0] = new Timer();
                Intent intent =new Intent(CountdownActivity.this,BackgroundService.class);
                startService(intent);

                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time--;
                                countdownView.setCountdown(time);
                                countdownView.setCountdownStatus(CountdownView.COUNTDOWN_START);
                                if (time == 0) {
                                    timer[0].cancel();

                                }
                            }
                        });
                    }
                }, 1000, 1000);
            }
        });
        button_Countdown_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countdownView.setCountdownStatus(CountdownView.COUNTDOWN_STOP);
                Intent intent =new Intent(CountdownActivity.this,BackgroundService.class);
                stopService(intent);
                timer[0].cancel();
            }
        });
    }
}