package com.abclauncher.powerboost.notification.fullycharged;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.notification.NotificationIntent;

/**
 * Created by sks on 2017/1/7.
 */

public class FullyChargedNotification {
    private static FullyChargedNotification mInstance;
    private Notification mNotification;
    private Context mContext;
    private static final String ACTION_CLICK_FULLY_CHARGED = "com.abclauncher.powerboost.fully.charged.clicked";
    private static final String ACTION_DELETE_FULLY_CHARGED = "com.abclauncher.powerboost.fully.charged.deleted";

    private FullyChargedNotification(Context context) {
        mContext = context;
    }
    public static void show(Context context){
        if(mInstance == null) {
            mInstance = new FullyChargedNotification(context);
        }
        mInstance.show();
    }

    private PendingIntent getMainPendingIntent(Context context) {
       /* Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("start", "charge");
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(context, 1000, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return notifyPendingIntent;*/

        Intent intent = new Intent();
        intent.setAction(ACTION_CLICK_FULLY_CHARGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 7, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void show() {
        if(mNotification == null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    //.setOngoing(true)
                    .setContentIntent(getMainPendingIntent(mContext))
                    .setContentTitle(mContext.getResources().getString(R.string.fully_charged_title))
                    .setContentText(mContext.getResources().getString(R.string.fully_charged_tips))
                    .setDeleteIntent(getDeletePendingIntent(mContext))
                    .setSmallIcon(R.drawable.ic_fully_charged)
                    .setAutoCancel(true);
            mNotification = mBuilder.build();
            //mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        }
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationIntent.RECEIVER_ACTION_FULLY_CHARGED_ID, mNotification);
    }

    private PendingIntent getDeletePendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_FULLY_CHARGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 8, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
