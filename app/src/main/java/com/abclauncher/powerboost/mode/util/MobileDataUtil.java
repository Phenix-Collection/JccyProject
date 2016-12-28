package com.abclauncher.powerboost.mode.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;


/**
 * Created by sks on 2016/12/27.
 */

public class MobileDataUtil {
    private static MobileDataUtil sMobileDataUtil;
    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private Context mContext;

    public static MobileDataUtil getInstance(Context context) {
        if (sMobileDataUtil == null){
            sMobileDataUtil = new MobileDataUtil(context);
        }
        return sMobileDataUtil;
    }
    private MobileDataUtil(Context context) {
        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setMobileData(boolean pBoolean) {
        if (getMobileDataState() == pBoolean) return;
        try {
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[]{boolean.class};
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, pBoolean);
        } catch (Exception e) {
        }
    }

    /**
     * 返回手机移动数据的状态
     * @return true 连接 false 未连接
     */
    public boolean getMobileDataState() {
        try {
            Class ownerClass = mConnectivityManager.getClass();
            Method method = ownerClass.getMethod("getMobileDataEnabled", new Class[0]);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, new Object[0]);
            return isOpen;
        } catch (Exception e) {
            Log.e("MobileData","getMobileDataState");
            return false;
        }
    }
}
