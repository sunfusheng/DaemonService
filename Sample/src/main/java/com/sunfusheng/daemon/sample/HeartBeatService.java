package com.sunfusheng.daemon.sample;

import android.util.Log;

import com.sunfusheng.daemon.AbsHeartBeatService;

/**
 * @author sunfusheng on 2018/8/3.
 */
public class HeartBeatService extends AbsHeartBeatService {
    private static final String TAG = "---> HeartBeatService";

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
        Log.d(TAG, "onHeartBeat()");
    }
}
