package com.scut.focuslearning;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.scut.focuslearning.Utils.BackgroundUtil;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    public static final String TAG = "BackgroundService";
    private boolean notify = false; //通知用户回到app专注
    public static String CHANNEL_1 = "channel1";

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "开启StatusService");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                boolean front = BackgroundUtil.queryUsageStats(getApplicationContext(), getPackageName());
                if (front) {
                    System.out.println("在前台");
                    notify = false;
                } else {
                    System.out.println("在后台");
                    if (!notify && BackgroundUtil.isHome(getApplicationContext())) {
                        notify = true;
                        Intent intent = new Intent(getApplicationContext(), CountdownActivity.class);
                        PendingIntent fullScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1, "通知渠道1", NotificationManager.IMPORTANCE_HIGH);
                        manager.createNotificationChannel(channel);
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channel.getId())
                                .setContentTitle("请立即返回app")
                                .setContentText("否则计时会中断")
                                .setWhen(System.currentTimeMillis())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_CALL)
                                .setContentIntent(fullScreenIntent)
//                                .setFullScreenIntent(fullScreenIntent, true)
                                .setSmallIcon(R.mipmap.ic_launcher).build();
                        manager.notify(1, notification);
                    } else if(!BackgroundUtil.isHome(getApplicationContext())){ // 打开其他app
                        Intent intent = new Intent(getApplicationContext(), CountdownActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent);
                    }
                }
            }
        }, 1000, 5000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }


}