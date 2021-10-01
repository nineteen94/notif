package com.rn_episode1.Worker;

import static com.rn_episode1.Util.Helpers.enqueueNonUIWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WorkManagerStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        enqueueNonUIWork(context);
    }
}
