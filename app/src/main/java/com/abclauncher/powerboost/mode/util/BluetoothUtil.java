package com.abclauncher.powerboost.mode.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by sks on 2016/12/27.
 */

public class BluetoothUtil {
    private static BluetoothUtil sBluetoothUtil;
    private WifiManager mWifiManager;
    private Context mContext;

    public static BluetoothUtil getInstance(Context context) {
        if (sBluetoothUtil == null){
            sBluetoothUtil = new BluetoothUtil(context);
        }
        return sBluetoothUtil;
    }

    private BluetoothUtil(Context context){
        mContext = context;
    }

    public boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) return false;
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    //获取蓝牙当前状态
    public boolean getBluetoothStatus() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            return defaultAdapter.getState() == BluetoothAdapter.STATE_ON;
        }
        return false;
    }
}
