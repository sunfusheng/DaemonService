package com.sunfusheng.daemon;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

/**
 * @author sunfusheng on 2018/8/1.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private static final String TAG = "---> JobService";
    private static final int JOB_ID = 10000;

    public static void scheduleJobService(Context context) {
        boolean isSuccess = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobSchedulerService.class));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMinimumLatency(DaemonUtil.getIntervalTime());
                builder.setOverrideDeadline(DaemonUtil.getIntervalTime() * 2);
                builder.setBackoffCriteria(DaemonUtil.getIntervalTime(), JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
            } else {
                builder.setPeriodic(DaemonUtil.getIntervalTime());
            }
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.cancelAll();
                isSuccess = jobScheduler.schedule(builder.build()) == JobScheduler.RESULT_SUCCESS;
            }
        }
        if (isSuccess) {
            Log.d(TAG, "Scheduler Success!");
        } else {
            Log.e(TAG, "Scheduler Failed!");
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob()");
        if (!DaemonUtil.isServiceRunning(DaemonHolder.getContext(), "com.sunfusheng.daemon.LocalService")) {
            Toast.makeText(this, "JobService onStartJob()", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, AbsWorkService.class));
        } else {
            Toast.makeText(this, "Service is running.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "JobService onStopJob()", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStopJob()");
        if (!DaemonUtil.isServiceRunning(DaemonHolder.getContext(), "com.sunfusheng.daemon.LocalService")) {
            startService(new Intent(this, AbsWorkService.class));
        }
        return false;
    }
}
