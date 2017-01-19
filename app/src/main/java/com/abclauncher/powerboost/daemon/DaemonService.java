package com.abclauncher.powerboost.daemon;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by xq on 2016/9/28.
 */
public class DaemonService extends Service {

    private static final int NOTIFICATION_ID = 0xFFFF;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, new Notification());
        stopSelf();
        Log.d(getClass().getSimpleName(), "-->onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    public static void setForeground(Service service) {
        service.startForeground(NOTIFICATION_ID, new Notification());
        service.startService(new Intent(service, DaemonService.class));
    }
}
