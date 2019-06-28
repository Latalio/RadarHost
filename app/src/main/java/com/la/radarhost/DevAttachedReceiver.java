package com.la.radarhost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DevAttachedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
        // todo there should do some judgement firstly
        NoDevActivity activity = (NoDevActivity)context;
        activity.finish();
    }
}