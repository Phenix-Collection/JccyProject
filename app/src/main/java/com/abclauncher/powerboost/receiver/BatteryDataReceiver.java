package com.abclauncher.powerboost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.abclauncher.powerboost.util.SettingsHelper;

/**
 * Created by sks on 2017/1/4.
 */

public class BatteryDataReceiver {
    private static final String TAG = "BatteryDataReceiver";
    private static final int REFRESH_BRIGHTNESS = 2;
    private BroadcastReceiver batteryReceiver;
    private BatteryCallback mBatteryCallback;
    private Context mContext;

    public BatteryDataReceiver(Context context, final BatteryCallback batteryCallback){
        mBatteryCallback = batteryCallback;
        mContext = context;

        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String getAction = intent.getAction();
                switch (getAction) {
                    case Intent.ACTION_BATTERY_CHANGED:
                        if (batteryCallback != null) {
                            batteryCallback.receiveBatteryData(intent);
                        }
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);


        context.registerReceiver(batteryReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(batteryReceiver);
        //mBatteryCallback = null;
    }

    public interface BatteryCallback{
        public void receiveBatteryData(Intent intent);
    }
}
