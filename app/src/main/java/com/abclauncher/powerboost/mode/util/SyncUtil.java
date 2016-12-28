package com.abclauncher.powerboost.mode.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by sks on 2016/12/27.
 */

public class SyncUtil {

    private static SyncUtil sSyncUtil;
    private Context mContext;

    public static SyncUtil getInstance(Context context) {
        if (sSyncUtil == null){
            sSyncUtil = new SyncUtil(context);
        }
        return sSyncUtil;
    }

    private SyncUtil(Context context){
        mContext = context;
    }
    public boolean getSyncStatus() {
        ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getBackgroundDataSetting() && ContentResolver.getMasterSyncAutomatically();
    }

    public void setSyncStatus(boolean enbled) {
        /*getMasterSyncAutomatically和setMasterSyncAutomatically为抽象类ContentResolver的静态函数，
         * 所以可以直接通过类来调用
         */
        ContentResolver.setMasterSyncAutomatically(enbled);
    }
}
