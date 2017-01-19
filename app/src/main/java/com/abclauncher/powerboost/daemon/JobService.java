package com.abclauncher.powerboost.daemon;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.abclauncher.powerboost.service.BatteryService;

/**
 * Created by xq on 2016/10/18.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobService extends android.app.job.JobService {

    private static final int JOB_ID = 1;
    private static final int JOB_PERIODIC = 5 * 1000;

    private static boolean mStopped = true;

    @Override
    public boolean onStartJob(JobParameters params) {
        BatteryService.start(this);
        return mStopped;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        BatteryService.start(this);
        return mStopped;
    }

    public static void stop() {
        mStopped = true;
    }

    public static void start(Context context) {
        if(mStopped) {
            try {
                mStopped = false;
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobService.class));
                builder.setPeriodic(JOB_PERIODIC);
                builder.setPersisted(true);
                JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                scheduler.schedule(builder.build());
            } catch (Exception e) {
                Log.e(JobService.class.getSimpleName(), e.getMessage(), e);
            }
        }
    }
}
