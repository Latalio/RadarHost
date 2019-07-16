package com.la.radarhost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DevAttachedReceiver extends BroadcastReceiver {
    private final String TAG = DevAttachedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        // todo there should do some judgement firstly
        Log.e(TAG, "Broadcast received");
        NoDevActivity activity = (NoDevActivity)context;
        activity.finish();
    }
}