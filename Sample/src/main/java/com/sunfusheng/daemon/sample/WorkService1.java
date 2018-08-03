package com.sunfusheng.daemon.sample;

import android.util.Log;

import com.sunfusheng.daemon.AbsWorkService;

/**
 * @author sunfusheng on 2018/8/3.
 */
public class WorkService1 extends AbsWorkService {
    private static final String TAG = "---> WorkService1";

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
        return 10 * 1000;
    }

    @Override
    public void onHeartBeat() {
        Log.d(TAG, "1、onHeartBeat()");
    }
}
