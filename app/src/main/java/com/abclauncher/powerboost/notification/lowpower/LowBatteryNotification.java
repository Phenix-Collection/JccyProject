package com.abclauncher.powerboost.notification.lowpower;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.notification.NotificationIntent;

/**
 * Created by sks on 2017/1/7.
 */

public class LowBatteryNotification {
    private static final String ACTION_CLICK_LOW_POWER = "com.abclauncher.powerboost.low.power.clicked";
    private static final String ACTION_DELETE_LOW_POWER = "com.abclauncher.powerboost.low.power.deleted";
    private static LowBatteryNotification mInstance;
    private Notification mNotification;
    private Context mContext;

    private LowBatteryNotification(Context context) {
        mContext = context;
    }
    public static void show(Context context){
        if(mInstance == null) {
            mInstance = new LowBatteryNotification(context);
        }
        mInstance.show();
    }

    private PendingIntent getMainPendingIntent(Context context) {
      /*  Intent intent = new Intent(context, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("start", "charge");
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(context, 1001, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return notifyPendingIntent;*/

        Intent intent = new Intent();
        intent.setAction(ACTION_CLICK_LOW_POWER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void show() {
        if(mNotification == null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    //.setOngoing(true)
                    .setContentIntent(getMainPendingIntent(mContext))
                    .setContentTitle(mContext.getResources().getString(R.string.low_battery_title))
                    .setContentText(mContext.getResources().getString(R.string.low_battery_warn))
                    .setDeleteIntent(getDeletePendingIntent(mContext))
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.low_battery_noti_icon30))
                    .setSmallIcon(R.drawable.low_battery_noti_icon30)
                    .setAutoCancel(true);
                    mNotification = mBuilder.build();
            //mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        }
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationIntent.RECEIVER_ACTION_LOW_BATTERY_ID, mNotification);
    }

    private PendingIntent getDeletePendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_LOW_POWER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
