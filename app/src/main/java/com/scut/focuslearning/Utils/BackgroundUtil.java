package com.scut.focuslearning.Utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BackgroundUtil {
    private static List<String> homePackageList;
    public static boolean queryUsageStats(Context context, String packageName) {
        String currentTopPackage = getCurrentTopPackageName(context);
        if (currentTopPackage.equals(packageName)) {
            return true;
        } else {
            return false;
        }
    }

    private static String getCurrentTopPackageName(Context context) {
        class RecentUseComparator implements Comparator<UsageStats> {

            @Override
            public int compare(UsageStats o1, UsageStats o2) {
                long LastTimeUsed1 = o1.getLastTimeUsed();
                long LastTimeUsed2 = o2.getLastTimeUsed();
                if (LastTimeUsed1 > LastTimeUsed2) {
                    return -1;
                } else if (LastTimeUsed1 == LastTimeUsed2) {
                    return 0;
                } else {
                    return 1;
                }

            }
        }
        RecentUseComparator recentComp = new RecentUseComparator();
        long now = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 100000 * 10, now);
        if (usageStats == null || usageStats.size() == 0) {
            if (HavaPermissionForTest(context) == false) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "权限不够\n请打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾", Toast.LENGTH_SHORT).show();
            }
            return "";
        }
        Collections.sort(usageStats, recentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        return currentTopPackage;
    }

    /**
     * 判断是否有用权限
     *
     * @param context 上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(Context context) {
        String packageName = getCurrentTopPackageName(context);
        if(homePackageList==null) {
            homePackageList = getHomes(context);
        }
        return homePackageList.contains(packageName);
    }

}

