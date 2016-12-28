package com.abclauncher.powerboost.mode.util;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by sks on 2016/12/27.
 */

public class GPSUtil {
    private static GPSUtil mGPSUtil;
    private Context mContext;

    public static GPSUtil getInstance(Context context) {
        if (mGPSUtil == null){
            mGPSUtil = new GPSUtil(context);
        }
        return mGPSUtil;
    }

    private GPSUtil(Context context){
        mContext = context;
    }

    //打开或关闭Gps
    public void setGpsStatus(Context context, boolean enabled) {
        Settings.Secure.setLocationProviderEnabled(context.getContentResolver(),
                LocationManager.GPS_PROVIDER, enabled);
    }

    //获取Gps开启或关闭状态
    public boolean getGpsStatus(Context context) {
        boolean status = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
                LocationManager.GPS_PROVIDER);
        return status;
    }
}
