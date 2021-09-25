package com.rn_episode1.Worker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkManagerStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PeriodicWorkRequest periodicWorker = new PeriodicWorkRequest.Builder(
                MainWorker.class, 15, TimeUnit.MINUTES
        ).build();

        WorkManager workManager = WorkManager.getInstance(context);

        workManager.enqueueUniquePeriodicWork("PeriodicWorker", ExistingPeriodicWorkPolicy.REPLACE, periodicWorker);
    }
}
