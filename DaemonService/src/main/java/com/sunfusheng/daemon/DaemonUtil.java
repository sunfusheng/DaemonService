package com.sunfusheng.daemon;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonUtil {
    private static final long INTERVAL_TIME = 30 * 1000;
    private static final String BRAND = Build.BRAND.toLowerCase();
    private static ActivityManager activityManager;

    public static ActivityManager getActivityManager(Context context) {
        if (activityManager == null) {
            activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return activityManager;
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : getActivityManager(context).getRunningServices(100)) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProcessRunning(Context context, String processName) {
        for (ActivityManager.RunningAppProcessInfo processInfo : getActivityManager(context).getRunningAppProcesses()) {
            if (processInfo.processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProcessRunning(Context context) {
        return isProcessRunning(context, getProcessName(context));
    }

    public static String getProcessName(Context context, int pid) {
        for (ActivityManager.RunningAppProcessInfo processInfo : getActivityManager(context).getRunningAppProcesses()) {
            if (processInfo != null && processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public static String getProcessName(Context context) {
        return getProcessName(context, android.os.Process.myPid());
    }

    public static long getIntervalTime() {
        return INTERVAL_TIME;
    }

    public static boolean isXiaomi() {
        return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
    }

    public static boolean isVivo() {
        return BRAND.contains("vivo") || BRAND.contains("bbk");
    }

    public static boolean isOppo() {
        return BRAND.contains("oppo");
    }

    public static boolean isHuawei() {
        return BRAND.contains("huawei") || BRAND.contains("honor");
    }
}
