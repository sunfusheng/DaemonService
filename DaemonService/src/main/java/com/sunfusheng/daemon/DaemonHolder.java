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
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonHolder {
    private static final String TAG = "---> DaemonHolder";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static final Map<Class<? extends Service>, Pair<String, ServiceConnection>> mServiceMap = new HashMap<>();

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
        if (!mServiceMap.containsKey(service)) {
            mServiceMap.put(service, new Pair<String, ServiceConnection>(service.getCanonicalName(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }));
            Log.d(TAG, "添加服务 " + service.getCanonicalName());
        }

        if (!DaemonUtil.isServiceRunning(getContext(), service.getCanonicalName())) {
            Intent intent = new Intent(getContext(), service);
            getContext().startService(intent);
            ServiceConnection serviceConnection = getServiceConnection(service);
            if (serviceConnection != null) {
                getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            Log.d(TAG, "启动服务 " + service.getCanonicalName());
        }
    }

    public static void stopService(Class<? extends Service> service) {
        if (mServiceMap.containsKey(service)) {
            mServiceMap.remove(service);
            Log.d(TAG, "删除服务 " + service.getCanonicalName());
        }

        if (DaemonUtil.isServiceRunning(getContext(), service.getCanonicalName())) {
            Intent intent = new Intent(getContext(), service);
            getContext().stopService(intent);
            ServiceConnection serviceConnection = getServiceConnection(service);
            if (serviceConnection != null) {
                getContext().unbindService(serviceConnection);
            }
            Log.d(TAG, "停止服务 " + service.getCanonicalName());
        }
    }

    public static void startService() {
        if (mServiceMap.size() > 0) {
            for (Map.Entry<Class<? extends Service>, Pair<String, ServiceConnection>> service : mServiceMap.entrySet()) {
                ServiceConnection serviceConnection = getServiceConnection(service.getKey());
                if (serviceConnection != null && !DaemonUtil.isServiceRunning(getContext(), getServiceCanonicalName(service.getKey()))) {
                    Intent intent = new Intent(getContext(), service.getKey());
                    getContext().startService(intent);
                    getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    Log.d(TAG, "重新启动服务 " + service.getValue());
                }
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            JobSchedulerService.scheduleJobService(context);
//        }
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

    private static String getServiceCanonicalName(Class<? extends Service> service) {
        if (mServiceMap.size() > 0 && mServiceMap.containsKey(service)) {
            Pair<String, ServiceConnection> pair = mServiceMap.get(service);
            if (pair != null) {
                return pair.first;
            }
        }
        return null;
    }

    private static ServiceConnection getServiceConnection(Class<? extends Service> service) {
        if (mServiceMap.size() > 0 && mServiceMap.containsKey(service)) {
            Pair<String, ServiceConnection> pair = mServiceMap.get(service);
            if (pair != null) {
                return pair.second;
            }
        }
        return null;
    }
}
