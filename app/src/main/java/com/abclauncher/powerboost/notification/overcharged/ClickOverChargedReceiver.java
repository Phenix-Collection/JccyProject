package com.abclauncher.powerboost.notification.overcharged;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.util.SettingsHelper;

/**
 * Created by sks on 2017/1/18.
 */

public class ClickOverChargedReceiver extends BroadcastReceiver {

    private String TAG = "ClickCleanTipsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        SettingsHelper.setDeleteOverChargedNotificationTime(context, System.currentTimeMillis());
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("start", "charge");
        context.startActivity(intent1);
    }
}
