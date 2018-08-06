package com.sunfusheng.daemon;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonHolder {
    private static final String TAG = "---> DaemonHolder";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    static Class<? extends Service> mService;
    private static String mServiceCanonicalName;
     static final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected() 已绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected() 已解绑");
            startService();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            onServiceDisconnected(name);
        }
    };

    private DaemonHolder() {
    }

    public static void init(Context context, Class<? extends AbsHeartBeatService> service) {
        mContext = context;
        mService = service;
        mServiceCanonicalName = service.getCanonicalName();
    }

    public static void startService(Class<? extends Service> service) {
        if (service != null) {
            mService = service;
            mServiceCanonicalName = service.getCanonicalName();
            startService();
        }
    }

    public static void startService() {
        if (mContext != null && mService != null && !DaemonUtil.isServiceRunning(mContext, mServiceCanonicalName)) {
            Intent intent = new Intent(mContext, mService);
            mContext.startService(intent);
//            mContext.bindService(intent, serviceConnection, Context.BIND_IMPORTANT);

            mContext.bindService(new Intent(mContext, DaemonService.class), serviceConnection, Context.BIND_IMPORTANT);
            Log.d(TAG, "启动服务");
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            JobSchedulerService.scheduleJobService(context);
//        }
    }

    public static void stopService() {
        if (mContext != null && mService != null && DaemonUtil.isServiceRunning(mContext, mServiceCanonicalName)) {
            mContext.stopService(new Intent(mContext, mService));
//            mContext.unbindService(serviceConnection);
            Log.d(TAG, "停止服务");
        }
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
