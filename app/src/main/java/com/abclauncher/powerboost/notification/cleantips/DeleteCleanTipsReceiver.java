package com.abclauncher.powerboost.notification.cleantips;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abclauncher.powerboost.util.SettingsHelper;

/**
 * Created by sks on 2017/1/18.
 */

public class DeleteCleanTipsReceiver extends BroadcastReceiver {

    private String TAG = "DeleteCleanTipsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        SettingsHelper.setDeleteCleanTipsNotificationTime(context, System.currentTimeMillis());
    }
}
