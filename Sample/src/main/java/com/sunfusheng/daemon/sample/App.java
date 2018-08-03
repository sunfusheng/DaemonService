package com.sunfusheng.daemon.sample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sunfusheng.daemon.DaemonHolder;
import com.sunfusheng.daemon.DaemonUtil;

/**
 * @author sunfusheng on 2018/8/1.
 */
public class App extends Application {
    private static final String TAG = "---> App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() 【" + DaemonUtil.getProcessName(this) + "】");
        DaemonHolder.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory()");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate()");
    }
}
