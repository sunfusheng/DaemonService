package com.sunfusheng.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author sunfusheng on 2018/8/3.
 */
public class DaemonReceiver extends BroadcastReceiver {
    private static final String TAG = "---> DaemonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.d(TAG, "onReceive() action: " + intent.getAction());
        }
        DaemonHolder.startService();
    }
}
