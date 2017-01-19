package com.abclauncher.powerboost.notification.cleantips;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.clean.bean.AppProcessInfo;
import com.abclauncher.powerboost.notification.NotificationIntent;
import com.abclauncher.powerboost.util.Utils;

import java.util.List;

/**
 * Created by sks on 2017/1/17.
 */

public class CleanTipsNotification {

    private static CleanTipsNotification mInstance;
    private final RemoteViews mRemoteViews;
    private Notification mNotification;
    private Context mContext;
    private List<AppProcessInfo> appProcessInfos;
    private final String ACTION_DELETE_CLEAN_TIPS = "com.abclauncher.powerboost.clean.tips.deleted";
    private final String ACTION_CLICK_CLEAN_TIPS = "com.abclauncher.powerboost.clean.tips.clicked";

    private CleanTipsNotification(Context context, List<AppProcessInfo> list) {
        mContext = context;
        appProcessInfos = list;

        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_clean_tip);

        mRemoteViews.setImageViewBitmap(R.id.app_one, Utils.drawableToBitmap(list.get(0).icon));
        mRemoteViews.setImageViewBitmap(R.id.app_two, Utils.drawableToBitmap(list.get(1).icon));
        mRemoteViews.setImageViewBitmap(R.id.app_three, Utils.drawableToBitmap(list.get(2).icon));
        mRemoteViews.setTextViewText(R.id.apps_consume_power,
                list.size() + " ");

        //mRemoteViews.setOnClickPendingIntent(R.id.root_view, getPendingIntent(mContext));
    }

    private PendingIntent getPendingIntent(Context context) {
       /* Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("start", "clean");
        intent.setAction(ACTION_DELETE_CLEAN_TIPS);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(context, 2004, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        return notifyPendingIntent;*/

        Intent intent = new Intent();
        intent.setAction(ACTION_CLICK_CLEAN_TIPS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public static void show(Context context, List<AppProcessInfo> list){
        if(mInstance == null) {
            mInstance = new CleanTipsNotification(context, list);
        }
        if (list.size() > 0) {
            mInstance.show();
        }
    }

    private void show() {
        if(mNotification == null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    //.setOngoing(true)
                    .setContentIntent(getPendingIntent(mContext))
                    .setDeleteIntent(getDeletePendingIntent(mContext))
                    .setContent(mRemoteViews)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
            mNotification = mBuilder.build();
        }
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationIntent.RECEIVER_ACTION_CLEAN_TIPS_ID, mNotification);
    }

    private PendingIntent getDeletePendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_CLEAN_TIPS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public static void cancelNotification(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationIntent.RECEIVER_ACTION_CLEAN_TIPS_ID);
    }


}
