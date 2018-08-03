package com.sunfusheng.daemon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * 远程进程服务
 *
 * @author sunfusheng on 2018/8/1.
 */
public class DaemonService extends Service {
    private static final String TAG = "---> RemoteService";
    private ScreenBroadcastReceiver screenBroadcastReceiver = new ScreenBroadcastReceiver();

    private final DaemonAidl aidl = new DaemonAidl.Stub() {
        @Override
        public void startService() throws RemoteException {
            Log.d(TAG, "startService()");
            DaemonService.this.startService(new Intent(DaemonService.this, AbsWorkService.class));
        }

        @Override
        public void stopService() throws RemoteException {
            Log.d(TAG, "stopService()");
            DaemonService.this.stopService(new Intent(DaemonService.this, AbsWorkService.class));
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(DaemonService.this, "已绑定 LocalService", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(DaemonService.this, "已断开绑定 LocalService", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onServiceDisconnected()");
            startLocalService();
            bindLocalService();
        }
    };

    private void startLocalService() {
        try {
            aidl.startService();
        } catch (RemoteException e) {
            Log.e(TAG, "startRemoteService()");
            e.printStackTrace();
        }
    }

    private void bindLocalService() {
        Intent intent = new Intent(this, AbsWorkService.class);
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        startLocalService();
        bindLocalService();

        listenNetworkConnectivity();
        screenBroadcastReceiver.registerScreenBroadcastReceiver(this);
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
        unbindService(serviceConnection);
        DaemonHolder.restartService(getApplicationContext(), getClass());
        screenBroadcastReceiver.unregisterScreenBroadcastReceiver(this);
    }

    private void listenNetworkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        Log.e(TAG, "onAvailable()");
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        Log.e(TAG, "onUnavailable()");
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        Log.e(TAG, "onLost()");
                    }
                });
            }
        }
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private boolean isRegistered = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                Log.e(TAG, "ACTION_SCREEN_ON");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                Log.e(TAG, "ACTION_SCREEN_OFF");
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                Log.e(TAG, "ACTION_USER_PRESENT");
            } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) { // Home键
                Log.e(TAG, "ACTION_CLOSE_SYSTEM_DIALOGS");
            }
        }

        public void registerScreenBroadcastReceiver(Context context) {
            if (!isRegistered) {
                isRegistered = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_USER_PRESENT);
                filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.registerReceiver(ScreenBroadcastReceiver.this, filter);
            }
        }

        public void unregisterScreenBroadcastReceiver(Context context) {
            if (isRegistered) {
                isRegistered = false;
                context.unregisterReceiver(ScreenBroadcastReceiver.this);
            }
        }
    }
}
