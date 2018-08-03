package com.sunfusheng.daemon.sample;

import android.util.Log;

import com.sunfusheng.daemon.AbsWorkService;

/**
 * @author sunfusheng on 2018/8/3.
 */
public class WorkService2 extends AbsWorkService {
    private static final String TAG = "---> WorkService2";

    @Override
    public void onStartService() {
        Log.d(TAG, "onStartService()");
    }

    @Override
    public void onStopService() {
        Log.e(TAG, "onStopService()");
    }

    @Override
    public long getHeartBeatMillis() {
        return 40 * 1000;
    }

    @Override
    public void onHeartBeat() {
        Log.d(TAG, "2、onHeartBeat()");
    }
}
