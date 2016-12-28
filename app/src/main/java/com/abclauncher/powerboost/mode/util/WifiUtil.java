package com.abclauncher.powerboost.mode.util;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by sks on 2016/12/27.
 */

public class WifiUtil {
    private static WifiUtil sWifiUtil;
    private WifiManager mWifiManager;
    private Context mContext;

    public static WifiUtil getInstance(Context context) {
        if (sWifiUtil == null){
            sWifiUtil = new WifiUtil(context);
        }
        return sWifiUtil;
    }

    private WifiUtil(Context context){
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void openWifi(){
        mWifiManager.setWifiEnabled(true);
    }

    public void closeWifi(){
        mWifiManager.setWifiEnabled(false);
    }
}
