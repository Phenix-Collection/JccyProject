package com.abclauncher.powerboost.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.receiver.BatteryDataReceiver;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.Utils;


/**
 * Created by sks on 2017/1/5.
 */

public class CleanNotification implements BatteryDataReceiver.BatteryCallback {
    private static final String TAG = "CleanNotification";
    private static final int REFRESH_BATTERY = 1;
    private static CleanNotification mInstance;
    private final Context mContext;
    private RemoteViews mRemoteViews;
    private BatteryDataReceiver mBatteryReceiver;
    private Notification mNotification;
    private boolean isCharging;
    private boolean mShowUsageTime;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REFRESH_BATTERY:
                    if (!CleanUtil.shouldCleanMemory(mContext)) {
                        updateUsageTime();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 500);
                    break;
            }
            return false;
        }
    });
    private NotificationCompat.Builder mBuilder;


    private CleanNotification(Context context) {
        mContext = context;

        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_clean);

        mRemoteViews.setOnClickPendingIntent(R.id.iv_clean,getPendingIntent(mContext));

        mBatteryReceiver = new BatteryDataReceiver(mContext, this);
    }


    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("start", "clean");
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return notifyPendingIntent;
    }

    private PendingIntent getMainPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(context, 1, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return notifyPendingIntent;
    }

    public static void show(Context context){
        if(mInstance == null) {
            mInstance = new CleanNotification(context);
        }
        mInstance.show();
    }

    private void show() {
        mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 1000);
        if(mNotification == null) {
            // 设置该通知优先级
            mBuilder = new NotificationCompat.Builder(mContext)
                    .setPriority(Notification.PRIORITY_MAX)// 设置该通知优先级
                    .setOngoing(true)
                    .setContent(mRemoteViews)
                    .setContentIntent(getMainPendingIntent(mContext))
                    .setColor(mContext.getResources().getColor(R.color.notification_color))
                    .setSmallIcon(R.mipmap.ic_launcher);
            mNotification = mBuilder.build();
            mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR | Notification.DEFAULT_VIBRATE;
        }
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationIntent.RECEIVER_ACTION_CLEAN_ID, mNotification);

    }

    @Override
    public void receiveBatteryData(Intent intent) {
        Log.d(TAG, "receiveBatteryData: ");
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int percent = LockerUtils.getBatteryPercent(level, scale);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging && percent != 100) {
          mRemoteViews.setTextViewText(R.id.time_left, mContext.getResources().getString( R.string.charging_time));
            mShowUsageTime = false;
            String hourValueStr = LockerUtils.getCharingHourValueStr(mContext, intent, percent);
            String minutesValueStr = LockerUtils.getCharingMinutesValueStr(mContext, intent, percent);
            mRemoteViews.setTextViewText(R.id.usage_time_hour_value, hourValueStr);
            mRemoteViews.setTextViewText(R.id.usage_time_minutes_value, minutesValueStr);
        } else {
          mRemoteViews.setTextViewText(R.id.time_left, mContext.getResources().getString( R.string.usage_time));
            mShowUsageTime = true;
            if (CleanUtil.shouldCleanMemory(mContext)) {
                String hour = Utils.getUsageHourValue(Utils.getUsageTime(mContext, percent));
                String mins = Utils.getUsageMinutesValue(Utils.getUsageTime(mContext, percent));
                mRemoteViews.setTextViewText(R.id.usage_time_hour_value, hour);
                mRemoteViews.setTextViewText(R.id.usage_time_minutes_value, mins);
            } else {
                String hour = Utils.getUsageHourValue(SettingsHelper.getUsageTime(mContext));
                String mins = Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(mContext));
                mRemoteViews.setTextViewText(R.id.usage_time_hour_value, hour);
                mRemoteViews.setTextViewText(R.id.usage_time_minutes_value, mins);
            }

        }

        mRemoteViews.setTextViewText(R.id.percent, percent + "%");
        Log.d(TAG, "receiveBatteryData: " + percent);
        if (mBuilder != null ) {
            int drawable = mContext.getResources().getIdentifier("stat_battery_" + percent, "drawable", mContext.getPackageName());
            mBuilder.setSmallIcon(drawable);
            mNotification = mBuilder.build();
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationIntent.RECEIVER_ACTION_CLEAN_ID, mNotification);
    }


    private void updateUsageTime() {
        if (mShowUsageTime){
            mRemoteViews.setTextViewText(R.id.time_left, mContext.getResources().getString( R.string.usage_time));
            String hour = Utils.getUsageHourValue(SettingsHelper.getUsageTime(mContext));
            String mins = Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(mContext));
            mRemoteViews.setTextViewText(R.id.usage_time_hour_value, hour);
            mRemoteViews.setTextViewText(R.id.usage_time_minutes_value, mins);

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationIntent.RECEIVER_ACTION_CLEAN_ID, mNotification);
        }
    }

    public static void cancelNotification(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationIntent.RECEIVER_ACTION_CLEAN_ID);
    }

    public class BatteryRefreshReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            updateUsageTime();
        }
    }
}
