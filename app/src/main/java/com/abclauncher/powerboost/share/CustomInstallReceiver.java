package com.abclauncher.powerboost.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.branch.referral.InstallListener;


/**
 * Created by shenjinliang on 16/11/18.
 */

public class CustomInstallReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CustomInstallReceiver", "onReceive: " + intent.getDataString());
        InstallListener listener = new InstallListener();
        listener.onReceive(context, intent);
    }
}
