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
public abstract class AbsWorkService extends Service {
    private static final String TAG = "---> AbsWorkService";

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
            Log.d(TAG, "startService()");
            AbsWorkService.this.startService(new Intent(AbsWorkService.this, DaemonService.class));
        }

        @Override
        public void stopService() throws RemoteException {
            Log.d(TAG, "stopService()");
            AbsWorkService.this.stopService(new Intent(AbsWorkService.this, DaemonService.class));
        }
    };

     private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected() 已绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected() 已解绑");
//            startDaemonService();
//            bindDaemonService();
        }
    };

    private void startDaemonService() {
        try {
            aidl.startService();
        } catch (RemoteException e) {
            Log.e(TAG, "startDaemonService()");
            e.printStackTrace();
        }
    }

    private void bindDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        onStartService();

        startDaemonService();
        bindDaemonService();

        if (getHeartBeatMillis() > 0) {
            timer.schedule(timerTask, getHeartBeatMillis(), getHeartBeatMillis());
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
        DaemonHolder.restartService(getApplicationContext(), getClass());

        timer.cancel();
        timerTask.cancel();
    }

    public abstract void onStartService();

    public abstract void onStopService();

    public abstract long getHeartBeatMillis();

    public abstract void onHeartBeat();
}
