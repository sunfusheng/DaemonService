package com.sunfusheng.daemon;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 当前进程服务
 *
 * @author sunfusheng on 2018/8/1.
 */
public abstract class AbsHeartBeatService extends Service {
    private static final String TAG = "---> HeartBeatService";

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            onHeartBeat();
        }
    };

    private final DaemonAidl aidl = new DaemonAidl.Stub() {
        @Override
        public void startService() throws RemoteException {
            Log.d(TAG, "aidl startService()");
        }

        @Override
        public void stopService() throws RemoteException {
            Log.e(TAG, "aidl stopService()");
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() 已绑定");
            try {
                service.linkToDeath(() -> {
                    Log.e(TAG, "onServiceConnected() linkToDeath");
                    try {
                        aidl.startService();
                        startBindService();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }, 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected() 已解绑");
            try {
                aidl.stopService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            onServiceDisconnected(name);
        }
    };

    private void startBindService() {
        try {
            startService(new Intent(this, DaemonService.class));
            bindService(new Intent(this, DaemonService.class), serviceConnection, Context.BIND_IMPORTANT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        onStartService();
        startBindService();
        if (getHeartBeatMillis() > 0) {
            timer.schedule(timerTask, getDelayExecutedMillis(), getHeartBeatMillis());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return (IBinder) aidl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        onStopService();

        unbindService(serviceConnection);
        DaemonHolder.restartService(getApplicationContext(), DaemonHolder.mService);

        try {
            timer.cancel();
            timer.purge();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onStartService();

    public abstract void onStopService();

    public abstract long getDelayExecutedMillis();

    public abstract long getHeartBeatMillis();

    public abstract void onHeartBeat();
}
