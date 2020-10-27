package com.baobab.user.baobabflyer.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;

public class JobService extends com.firebase.jobdispatcher.JobService {

    private static final String TAG = "JobService";

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "Performing long running task in scheduled job");

        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire(); // WakeLock 깨우기

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
