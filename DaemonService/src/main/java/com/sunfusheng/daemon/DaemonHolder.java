package com.sunfusheng.daemon;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonHolder {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static final List<Class<? extends Service>> mService = new ArrayList<>();

    private DaemonHolder() {
    }

    public static Context getContext() {
        if (mContext == null) {
            throw new NullPointerException("context is null.");
        }
        return mContext;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static void startService(Class<? extends Service> service) {
        if (!mService.contains(service)) {
            mService.add(service);
        }

        getContext().startService(new Intent(getContext(), service));
    }

    public static boolean startService() {
        if (mContext == null || mService.size() == 0) {
            return false;
        }

//        if (!DaemonUtil.isServiceRunning(context, "com.sunfusheng.daemon.LocalService")) {
//            context.startService(new Intent(context, LocalService.class));
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            JobSchedulerService.scheduleJobService(context);
//        }

        for (Class<? extends Service> service : mService) {

        }
        return true;
    }

    public static void restartService(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + DaemonUtil.getIntervalTime(), pendingIntent);
        }
    }
}
