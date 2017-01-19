package com.abclauncher.powerboost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abclauncher.powerboost.service.BatteryService;

/**
 * Created by sks on 2016/10/21.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        Log.d("BootCompletedReceiver", "onReceive: " + action);
        BatteryService.start(context);
    }
}
