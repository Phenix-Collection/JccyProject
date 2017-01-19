package com.abclauncher.powerboost.locker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;



public class LockerReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private BroadcastReceiver lockerReceiver;

    public LockerReceiver(Context context, final LockerReceiverCallback lockerRefreshCallback) {
        lockerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String getAction = intent.getAction();
                switch (getAction) {
                    case Intent.ACTION_TIMEZONE_CHANGED:
                    case Intent.ACTION_TIME_TICK:
                        if (lockerRefreshCallback != null) {
                            lockerRefreshCallback.receiveTime(LockerUtils.getNowTimeStr(context));
                            lockerRefreshCallback.receiveDate(LockerUtils.getDateStr(context));
                        }
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        if(PhoneCallReceiver.mIsUnlockedForCalling){
                            lockerRefreshCallback.receiveHomeClick();
                        }
                        break;
                    case Intent.ACTION_BATTERY_CHANGED:
                        if (lockerRefreshCallback != null) {
                            lockerRefreshCallback.receiveBatteryData(intent);
                        }
                        break;
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        if (lockerRefreshCallback != null) {
                            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                                lockerRefreshCallback.receiveHomeClick();
                            }
                        }
                        break;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        if (lockerRefreshCallback != null) {
        /*    lockerRefreshCallback.receiveCpuPercent(PreferenceUtils.getInstance(context)
                    .getIntParam(LockerConstansts.BATTERY_CUP_PERCENT_KEY, 10));
            lockerRefreshCallback.receiveTemperature(PreferenceUtils.getInstance(context)
                    .getIntParam(LockerConstansts.BATTERY_TEMPERATURE_KEY, 10));*/
            lockerRefreshCallback.receiveTime(LockerUtils.getNowTimeStr(context));
            lockerRefreshCallback.receiveDate(LockerUtils.getDateStr(context));
        }
        context.registerReceiver(lockerReceiver, intentFilter);
    }

    public void unregisterLockerReceiver(Context context) {
        if(lockerReceiver != null) {
            context.unregisterReceiver(lockerReceiver);
        }
    }
    public interface LockerReceiverCallback {
        void receiveCpuPercent(int cpuPercent);
        void receiveTemperature(int time);
        void receiveTime(String time);
        void receiveDate(String data);
        void receiveBatteryData(Intent intent);
        void receiveHomeClick();
    }
}
