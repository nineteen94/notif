package com.dexter_time.Worker;

import static com.dexter_time.Util.Helpers.enqueueNonUIWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WorkManagerStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        enqueueNonUIWork(context);
    }
}
