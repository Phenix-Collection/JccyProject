package com.abclauncher.powerboost.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.abclauncher.powerboost.MainActivity;
import com.abclauncher.powerboost.clean.bean.AppProcessInfo;
import com.abclauncher.powerboost.clean.utils.AppUtils;
import com.abclauncher.powerboost.daemon.DaemonService;
import com.abclauncher.powerboost.daemon.JobService;
import com.abclauncher.powerboost.daemon.NdkProcess;
import com.abclauncher.powerboost.locker.SmartLockerActivity;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.mode.util.BrightnessUtil;
import com.abclauncher.powerboost.notification.CleanNotification;
import com.abclauncher.powerboost.notification.fullycharged.FullyChargedNotification;
import com.abclauncher.powerboost.notification.lowpower.LowBatteryNotification;
import com.abclauncher.powerboost.notification.overcharged.OverBatteryNotification;
import com.abclauncher.powerboost.notification.verylowpower.VeryLowBatteryNotification;
import com.abclauncher.powerboost.notification.cleantips.CleanTipsNotification;
import com.abclauncher.powerboost.util.SettingsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/29/029.
 */

public class BatteryService extends Service{

    private static final String TAG = "BatteryService";
    private static final long FULLY_CHARGED_TIME_THRESHOLD = 1000 * 60 * 10;
    private static final long OVER_CHARGED_TIME_THRESHOLD = 1000 * 60 * 60;
    private static final long CLEAN_TIPS_TIME_THRESHOLD = 1000 * 60 * 60;
    private static final int REFRESH_BRIGHTNESS = 2;
    private boolean isCharging;
    private BatteryReceiver mBatteryReceiver;
    private static BatteryService mInstance;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REFRESH_BRIGHTNESS:

                    int curBrightnessPercent = BrightnessUtil.getInstance(getApplicationContext()).getCurBrightnessPercent();
                    int systemBrighnessPercent = SettingsHelper.getSystemBrighnessPercent(getApplicationContext());
                    Log.d(TAG, "handleMessage: curBrightnessPercent" + curBrightnessPercent);
                    Log.d(TAG, "handleMessage: mLastBrightnessPercent" + systemBrighnessPercent);
                    //当前亮度百分比和 之前记录的系统值如果不一致 那么重新计算
                    if(curBrightnessPercent != systemBrighnessPercent){
                        Log.d(TAG, "handleMessage: refresh usage time by brightness ");
                        //当前亮度百分比 减去 基础百分比
                        int brightness = curBrightnessPercent - systemBrighnessPercent;

                        long brightnessTime = (long) (BRIGHTNESS_TIME * mBatteryPercent * brightness * 1.f /(100 * 100) );
                        long usageTime = SettingsHelper.getUsageTime(getApplicationContext());
                        Log.d(TAG, "setUsageTime: brightness--->" + brightness );
                        SettingsHelper.setUsageTime(getApplicationContext(), (long) (usageTime - brightnessTime));

                        //保存当前 亮度值
                        SettingsHelper.setSystemBrightnessPercent(getApplicationContext(), curBrightnessPercent);
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_BRIGHTNESS, 500);
                    break;
            }
            return false;
        }
    });
    private int mBatteryPercent;
    private static long FULL_USAGE_TIME = 1000 * 60 * 60 * 36;
    private static long WIFI_TIME = 1000 * 60 * 100;
    private static long BRIGHTNESS_TIME = 1000 * 60 * 100;
    private static long BLUETOOTH_TIME = 1000 * 60 * 60;
    Boolean mLastCharingStatus = null;
    private List<AppProcessInfo> apps = new ArrayList<>();

    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String getAction = intent.getAction();
            long usageTime = SettingsHelper.getUsageTime(getApplicationContext());
            switch (getAction) {
                case Intent.ACTION_SCREEN_OFF:
                    if (SettingsHelper.getLockScreenOpened(getApplicationContext())) {
                        Log.d(TAG, "onReceive: SmartLockerActivity" );
                        SmartLockerActivity.start(BatteryService.this);
                    }
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    Bundle bundle = intent.getExtras();
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                            || status == BatteryManager.BATTERY_STATUS_FULL;
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                    mBatteryPercent = LockerUtils.getBatteryPercent(level, scale);

                    setFullyChargedTime();

                    if (mLastCharingStatus == null) {
                        mLastCharingStatus = isCharging;
                    }
                    //如果之前状态 和当前状态不一致 而且当前状态为充电 那么可以判定插上充电器了
                    Log.d(TAG, "onReceive: mLastCharingStatus" + mLastCharingStatus);
                    Log.d(TAG, "onReceive: isCharging" + isCharging);
                    if (mLastCharingStatus != isCharging && isCharging && SettingsHelper.getAutoLaunchOpened(getApplicationContext())) {
                        startChargeActivity();
                    }
                    if (mLastCharingStatus != isCharging) {
                        mLastCharingStatus = isCharging;
                    }

                    //
                    if (!isCharging && mBatteryPercent == 30 && SettingsHelper.getLowBatteryOpened(getApplicationContext())) {
                        if (shouldShowLowBatteryNotification()) {
                            SettingsHelper.setDeleteLowPowerNotificationTime(getApplicationContext(), 0);
                            LowBatteryNotification.show(getApplicationContext());
                        }
                    }

                    if (!isCharging && (mBatteryPercent == 10) && SettingsHelper.getLowBatteryOpened(getApplicationContext())) {
                        if (shouldShowVeryLowBatteryNotification()) {
                            SettingsHelper.setDeleteVeryLowPowerNotificationTime(getApplicationContext(), 0);
                            VeryLowBatteryNotification.show(getApplicationContext());
                        }

                    }

                    if (shouldShowFullChargedNotification()) {//充满
                        if (hasDeleteFullyChargeOverOneHour()) {
                            SettingsHelper.setDeleteFullyChargedNotificationTime(getApplicationContext(), 0);
                            FullyChargedNotification.show(getApplicationContext());
                        }
                    }

                    if (shouldShowOverChargedNotification()) {//过冲
                        if (hasDeleteOverChargeOverOneHour()) {
                            SettingsHelper.setDeleteOverChargedNotificationTime(getApplicationContext(), 0);
                            OverBatteryNotification.show(getApplicationContext());
                        }

                    }

                    if (shouldShowCleanTipNotification()) {//显示 清理的通知
                        Log.d(TAG, "onReceive: " + "shouldShowCleanTipNotification");
                        apps.clear();
                        SettingsHelper.setDeleteCleanTipsNotificationTime(getApplicationContext(), 0);
                        new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppUtils.getRunningProcessInfo(getApplicationContext(), new AppUtils.ScanApps(){
                                            @Override
                                            public void scanApp(AppProcessInfo info) {
                                                if (apps.size() < 30) {
                                                    apps.add(info);
                                                }
                                            }
                                        });

                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (apps.size() >= 3) {
                                                    CleanTipsNotification.show(getApplicationContext(), apps);
                                                }
                                            }
                                        }, 2000);
                                    }
                                }
                        ).start();

                    }
                    break;
                case AudioManager.RINGER_MODE_CHANGED_ACTION:
                    int audioMuteState=intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE,AudioManager.RINGER_MODE_NORMAL);
                    switch (audioMuteState){
                        case AudioManager.RINGER_MODE_NORMAL:
                        case AudioManager.RINGER_MODE_VIBRATE:
                            //usage time 减少
                            Log.d(TAG, "setUsageTime: ringer--->");
                            SettingsHelper.setUsageTime(getApplicationContext(), (long) (usageTime - WIFI_TIME * mBatteryPercent * 1.f / 100));
                            break;
                        case AudioManager.RINGER_MODE_SILENT:
                            //usage time 增加
                            Log.d(TAG, "setUsageTime: ringer--->");
                            SettingsHelper.setUsageTime(getApplicationContext(), (long) (usageTime + WIFI_TIME * mBatteryPercent * 1.f / 100));
                            break;
                    }
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifistate=intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_DISABLED);
                    //开关一开一关 会导致重复添加了 俩次时间 所以我们吧
                    if (wifistate == WifiManager.WIFI_STATE_ENABLED){
                        //wifi 打开 减少时间
                        long curTime = SettingsHelper.getUsageTime(getApplicationContext());
                        Log.d(TAG, "setUsageTime: wifi--->");
                        SettingsHelper.setUsageTime(getApplicationContext(), (long) (curTime - WIFI_TIME * mBatteryPercent * 1.f / 200));
                    }else if (wifistate == WifiManager.WIFI_STATE_DISABLED){
                        //wifi 关闭  增加时间
                        long curTime = SettingsHelper.getUsageTime(getApplicationContext());
                        Log.d(TAG, "setUsageTime: wifi--->");
                        SettingsHelper.setUsageTime(getApplicationContext(), (long) (curTime + WIFI_TIME * mBatteryPercent * 1.f / 100));
                    }
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    Log.d(TAG, "onReceive:-- ACTION_CONNECTION_STATE_CHANGED" );
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
                    switch (bluetoothState) {
                        case BluetoothAdapter.STATE_CONNECTED:
                            //蓝牙链接 减少可使用时间
                            Log.d(TAG, "setUsageTime: Bluetooth--->");
                            SettingsHelper.setUsageTime(getApplicationContext(), (long) (usageTime - BLUETOOTH_TIME * mBatteryPercent * 1.f / 100));
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            //蓝牙关闭 增加可使用时间
                            Log.d(TAG, "setUsageTime: Bluetooth--->");
                            SettingsHelper.setUsageTime(getApplicationContext(), (long) (usageTime + BLUETOOTH_TIME * mBatteryPercent * 1.f / 100));
                            break;
                    }
                    break;
            }

        }
    }

    private boolean hasDeleteOverChargeOverOneHour() {
        if (SettingsHelper.getDeleteOverChargedNotificationTime(getApplicationContext()) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getDeleteOverChargedNotificationTime(getApplicationContext()) > CLEAN_TIPS_TIME_THRESHOLD)
            return true;
        return false;
    }

    private boolean hasDeleteFullyChargeOverOneHour() {
        if (SettingsHelper.getDeleteFullyChargedNotificationTime(getApplicationContext()) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getDeleteFullyChargedNotificationTime(getApplicationContext()) > CLEAN_TIPS_TIME_THRESHOLD)
            return true;
        return false;
    }

    private boolean shouldShowVeryLowBatteryNotification() {
        if (SettingsHelper.getDeleteVeryLowPowerNotificationTime(getApplicationContext()) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getDeleteVeryLowPowerNotificationTime(getApplicationContext()) > CLEAN_TIPS_TIME_THRESHOLD)
            return true;
        return false;
    }

    private boolean shouldShowLowBatteryNotification() {
        if (SettingsHelper.getDeleteCleanTipsNotificationTime(getApplicationContext()) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getDeleteLowPowerNotificationTime(getApplicationContext()) > CLEAN_TIPS_TIME_THRESHOLD)
            return true;
        return false;
    }

    private boolean shouldShowCleanTipNotification() {
        if (SettingsHelper.getLastCleanTime(getApplicationContext()) == 0) return false;
        if ((System.currentTimeMillis() - SettingsHelper.getLastCleanTime(getApplicationContext())) > CLEAN_TIPS_TIME_THRESHOLD
                && hasDeleteCleanTipsOverOneHour()) return true;
        return false;
    }

    private boolean hasDeleteCleanTipsOverOneHour() {
        if (SettingsHelper.getDeleteCleanTipsNotificationTime(getApplicationContext()) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getDeleteCleanTipsNotificationTime(getApplicationContext()) > CLEAN_TIPS_TIME_THRESHOLD)
            return true;
        return false;
    }

    private void startChargeActivity() {
        Log.d(TAG, "startChargeActivity: ");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("start", "charge");
        startActivity(intent);
    }

    private void setFullyChargedTime() {
        if (isCharging && mBatteryPercent == 100 && SettingsHelper.getFullyChargedTime(getApplicationContext()) == 0) {
            SettingsHelper.setFullyChargedTime(getApplicationContext(), System.currentTimeMillis());
        }

        if (!isCharging || mBatteryPercent != 100) {
            SettingsHelper.setFullyChargedTime(getApplicationContext(), 0);
        }
    }

    private boolean shouldShowFullChargedNotification() {

        if (isCharging && mBatteryPercent == 100 && SettingsHelper.getFullyChargedTime(getApplicationContext()) != 0) {
            Log.d(TAG, "shouldShowFullChargedNotification: " + (System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext())));
            return SettingsHelper.getFullyChargedOpened(getApplicationContext()) && System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext()) >= FULLY_CHARGED_TIME_THRESHOLD;
        }
        return false;
    }

    private boolean shouldShowOverChargedNotification() {

        if (isCharging && mBatteryPercent == 100 && SettingsHelper.getFullyChargedTime(getApplicationContext()) != 0) {
            return SettingsHelper.getFullyChargedOpened(getApplicationContext()) && System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext()) >= OVER_CHARGED_TIME_THRESHOLD;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaemonService.setForeground(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobService.start(this);
        } else {
            NdkProcess.start(this);
        }

        mInstance = this;
        openReceiver();

        if (SettingsHelper.getStatusBarOpened(getApplicationContext())) {
            CleanNotification.show(getApplicationContext());
        }

       /* LowBatteryNotification.show(getApplicationContext());
        VeryLowBatteryNotification.show(getApplicationContext());
        FullyChargedNotification.show(getApplicationContext());
        OverBatteryNotification.show(getApplicationContext());*/

        //初始化 当前系统亮度
        SettingsHelper.setSystemBrightnessPercent(getApplicationContext(), BrightnessUtil.getInstance(getApplicationContext()).getCurBrightnessPercent());
        //mLastBrightnessPercent = BrightnessUtil.getInstance(getApplicationContext()).getCurBrightnessPercent();

        mHandler.sendEmptyMessageDelayed(REFRESH_BRIGHTNESS, 1000);
    }

    public void openReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            mBatteryReceiver = new BatteryReceiver();
            registerReceiver(mBatteryReceiver, intentFilter);
        } catch (Exception e) {

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        if (mInstance == null) {
            Log.d(TAG, "start: ");
            context.startService(new Intent(context, BatteryService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInstance = null;
    }
}
