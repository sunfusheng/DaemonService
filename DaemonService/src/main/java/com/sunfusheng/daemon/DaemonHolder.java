package com.sunfusheng.daemon;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonHolder {
    private static final String TAG = "---> DaemonHolder";
    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    static Class<? extends Service> mService;
    private static String mServiceCanonicalName;

    private DaemonHolder() {
    }

    public static void init(Context context, Class<? extends AbsHeartBeatService> service) {
        mContext = context;
        mService = service;
        mServiceCanonicalName = service.getCanonicalName();
        startService();
    }

    public static void startService() {
        if (mContext != null && mService != null && !DaemonUtil.isServiceRunning(mContext, mServiceCanonicalName)) {
            try {
                mContext.startService(new Intent(mContext, mService));
                Log.d(TAG, "启动服务");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !DaemonUtil.isXiaomi()) {
                JobSchedulerService.scheduleJobService(mContext);
                Log.d(TAG, "启动 JobService");
            }

            mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(mContext.getPackageName(), mService.getName()),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static void stopService() {
        if (mContext != null && mService != null && DaemonUtil.isServiceRunning(mContext, mServiceCanonicalName)) {
            try {
                mContext.stopService(new Intent(mContext, mService));
                Log.d(TAG, "停止服务");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void restartService(Context context, Class<?> cls) {
        Log.d(TAG, "重启服务 AlarmManager");
        Intent intent = new Intent(context, cls);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + DaemonUtil.getIntervalTime(), pendingIntent);
        }
    }
}
