package com.abclauncher.powerboost.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.abclauncher.powerboost.mode.bean.MyModeSettings;
import com.abclauncher.powerboost.mode.util.AudioUtil;
import com.abclauncher.powerboost.mode.util.BluetoothUtil;
import com.abclauncher.powerboost.mode.util.BrightnessUtil;
import com.abclauncher.powerboost.mode.util.HapticFeedbackUtil;
import com.abclauncher.powerboost.mode.util.MobileDataUtil;
import com.abclauncher.powerboost.mode.util.ScreenOutUtil;
import com.abclauncher.powerboost.mode.util.SyncUtil;
import com.abclauncher.powerboost.mode.util.WifiUtil;


/**
 * Created by sks on 2016/9/18.
 */
public class SettingsHelper {
    public static final int MODE_GENERAL = 0;
    public static final int MODE_BATTERY = 1;
    public static final int MODE_SLEEP = 2;
    public static final int MODE_MY = 3;
    public static final String MODE_KEY = "mode_key";
    private static final String MOBILE_DATA_KEY = "mobile_data_key";
    private static final String WIFI_KEY = "wifi_key";
    private static final String SYNC_KEY = "sync_key";
    private static final String BLUETOOTH_KEY = "bluetooth_key";
    private static final String VIBRATE_KEY = "vibrate_key";
    private static final String HAPTIC_FEEDBACK_KEY = "haptic_feedback_key";
    private static final String BRIGHTNESS_IS_AUTO_KEY = "brightness_is_auto_key";
    private static final String RINGER_KEY = "ringer_key";
    private static final String MEDIA_VOLUME_KEY = "media_volume_key";
    private static final String BRIGHTNESS_KEY = "brightness_key";
    private static final String SYSTEM_BRIGHTNESS_KEY = "system_brightness_key";
    private static final String SCREEN_OUT_KEY = "screen_out_key";
    private static final String LAST_CLEAN_KEY = "last_clean_key";
    private static final String USAGE_TIME_KEY = "usage_time_key";
    private static final String CLEAN_TIMES_KEY = "clean_times_key";
    private static final String HAS_RATE_US = "has_rate_us";

    //settings key
    private static final String STATUS_BAR_KEY = "status_bar_key";
    private static final String FULLY_CHARGED_KEY = "fully_charged_key";
    private static final String LOW_BATTERY_KEY = "low_battery_key";
    private static final String OVER_CHARGED_KEY = "over_charged_key";
    private static final String AUTO_LAUNCH_KEY = "auto_launch_key";
    private static final String LOCK_SCREEN_KEY = "lock_screen_key";
    private static final String FULLY_CHARGED_TIME_KEY = "fully_charged_time_key";
    private static final String HAS_CREATE_SHORTCUT = "has_create_shortcut";
    private static final String DELETE_CLEAN_TIPS_NOTIFICATION = "delete_clean_tips_notification";
    private static final String DELETE_LOW_POWER_NOTIFICATION = "delete_low_power_notification";
    private static final String DELETE_VERY_LOW_POWER_NOTIFICATION = "delete_very_low_power_notification";
    private static final String DELETE_FULLY_CHARGED_NOTIFICATION = "delete_fully_charged_notification";
    private static final String DELETE_OVER_CHARGED_NOTIFICATION = "delete_over_charged_notification";
    private static final String TAG = "SettingsHelper";
    private static long FULL_USAGE_TIME = 1000 * 60 * 60 * 36;


    public SettingsHelper() {
    }

    public static SharedPreferences getSettingsSharedPreferences(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp;
    }

    public static SharedPreferences.Editor getSettingsEditor(Context context) {
        SharedPreferences sp = getSettingsSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        return editor;
    }

    public static String getPreferenceString(Context context, String name, String def) {
        SharedPreferences prefs = getSettingsSharedPreferences(context);
        return prefs.getString(name, def);
    }

    public static void setPreferenceString(Context context, String name, String value) {
        if(context != null) {
            SharedPreferences.Editor editPrefs = getSettingsEditor(context);
            editPrefs.putString(name, value);
            editPrefs.commit();
        }
    }

    public static int getPreferenceInt(Context context, String name, int def) {
        SharedPreferences prefs = getSettingsSharedPreferences(context);
        return prefs.getInt(name, def);
    }

    public static void setPreferenceInt(Context context, String name, int value) {
        SharedPreferences.Editor editPrefs = getSettingsEditor(context);
        editPrefs.putInt(name, value);
        editPrefs.commit();
    }

    public static long getPreferenceLong(Context context, String name, long def) {
        SharedPreferences prefs = getSettingsSharedPreferences(context);
        return prefs.getLong(name, def);
    }

    public static void setPreferenceLong(Context context, String name, long value) {
        SharedPreferences.Editor editPrefs = getSettingsEditor(context);
        editPrefs.putLong(name, value);
        editPrefs.commit();
    }

    public static Float getPreferenceFloat(Context context, String name, Float def) {
        SharedPreferences prefs = getSettingsSharedPreferences(context);
        return Float.valueOf(prefs.getFloat(name, def.floatValue()));
    }

    public static void setPreferenceFloat(Context context, String name, Float value) {
        SharedPreferences.Editor editPrefs = getSettingsEditor(context);
        editPrefs.putFloat(name, value.floatValue());
        editPrefs.commit();
    }

    public static boolean getPreferenceBoolean(Context context, String name, boolean def) {
        SharedPreferences prefs = getSettingsSharedPreferences(context);
        return prefs.getBoolean(name, def);
    }

    public static void setPreferenceBoolean(Context context, String name, boolean value) {
        SharedPreferences.Editor editPrefs = getSettingsEditor(context);
        editPrefs.putBoolean(name, value);
        editPrefs.commit();
    }

    public static void setCurrentMode(Context context, int mode){
        setPreferenceInt(context, MODE_KEY, mode);
    }

    public static int getCurrentMode(Context context){
        return  getPreferenceInt(context, MODE_KEY, MODE_MY);
    }

    public static void setCleanTimes(Context context, int times){
        setPreferenceInt(context, CLEAN_TIMES_KEY, times);
    }

    public static int getCleanTimes(Context context){
        return  getPreferenceInt(context, CLEAN_TIMES_KEY, 0);
    }

    public static boolean getHasRateUs(Context context){
        return getPreferenceBoolean(context, HAS_RATE_US, false);
    }
    public static void setHasRateUs(Context context, boolean value){
        setPreferenceBoolean(context, HAS_RATE_US, value);
    }

    public static boolean getMobileDataIsOpened(Context context){
        return getPreferenceBoolean(context, MOBILE_DATA_KEY, MobileDataUtil.getInstance(context).getMobileDataState());
    }
    public static void setMobileDataIsOpened(Context context, boolean value){
       setPreferenceBoolean(context, MOBILE_DATA_KEY, value);
    }

    public static boolean getWifiIsOpened(Context context){
        return getPreferenceBoolean(context, WIFI_KEY, WifiUtil.getInstance(context).getWifiOpened());
    }
    public static void setWifiIsOpened(Context context, boolean value){
        setPreferenceBoolean(context, WIFI_KEY, value);
    }

    public static boolean getSyncIsOpened(Context context){
        return getPreferenceBoolean(context, SYNC_KEY, SyncUtil.getInstance(context).getSyncStatus());
    }
    public static void setSyncIsOpened(Context context, boolean value){
        setPreferenceBoolean(context, SYNC_KEY, value);
    }

    public static boolean getBluetoothIsOpened(Context context){
        return getPreferenceBoolean(context, BLUETOOTH_KEY, BluetoothUtil.getInstance(context).getBluetoothStatus());
    }
    public static void setBluetoothIsOpened(Context context, boolean value){
        setPreferenceBoolean(context, BLUETOOTH_KEY, value);
    }

    public static boolean getVibrateIsOpened(Context context){
        return getPreferenceBoolean(context, VIBRATE_KEY, AudioUtil.getInstance(context).isVibrate());
    }
    public static void setVibrateIsOpened(Context context, boolean value){
        setPreferenceBoolean(context, VIBRATE_KEY, value);
    }

    public static boolean getHapticIsOpened(Context context){
        return getPreferenceBoolean(context, HAPTIC_FEEDBACK_KEY, HapticFeedbackUtil.getInstance(context).getHapticFeedbackEnable());
    }
    public static void setHapticIsOpened(Context context, boolean value){
        setPreferenceBoolean(context, HAPTIC_FEEDBACK_KEY, value);
    }

    public static boolean getBrightnessIsAuto(Context context){
        return getPreferenceBoolean(context, BRIGHTNESS_IS_AUTO_KEY, BrightnessUtil.getInstance(context).isAutoMode());
    }
    public static void setBrightnessIsAuto(Context context, boolean value){
        setPreferenceBoolean(context, BRIGHTNESS_IS_AUTO_KEY, value);
    }

    public static int getRingerPercent(Context context){
        return getPreferenceInt(context, RINGER_KEY, AudioUtil.getInstance(context).getPercentRingVolume());
    }
    public static void setRingerPercent(Context context, int value){
        setPreferenceInt(context, RINGER_KEY, value);
    }

    public static int getMediaVolumePercent(Context context){
        return getPreferenceInt(context, MEDIA_VOLUME_KEY, AudioUtil.getInstance(context).getPercentMediaVolume());
    }
    public static void setMeidaVolumePercent(Context context, int value){
        setPreferenceInt(context, MEDIA_VOLUME_KEY, value);
    }

    public static int getBrighnessPercent(Context context){
        return getPreferenceInt(context, BRIGHTNESS_KEY, BrightnessUtil.getInstance(context).getCurBrightnessPercent());
    }
    public static void setBrightnessPercent(Context context, int value){
        setPreferenceInt(context, BRIGHTNESS_KEY, value);
    }

    public static int getScreenOutPercent(Context context){
        return getPreferenceInt(context, SCREEN_OUT_KEY, ScreenOutUtil.getInstance(context).getScreenOutTimePercent());
    }
    public static void setScreenOutPercent(Context context, int value){
        setPreferenceInt(context, SCREEN_OUT_KEY, value);
    }

    public static int getSystemBrighnessPercent(Context context){
        return getPreferenceInt(context, SYSTEM_BRIGHTNESS_KEY, BrightnessUtil.getInstance(context).getCurBrightnessPercent());
    }
    public static void setSystemBrightnessPercent(Context context, int value){
        setPreferenceInt(context, SYSTEM_BRIGHTNESS_KEY, value);
    }


    public static long getLastCleanTime(Context context){
        return getPreferenceLong(context, LAST_CLEAN_KEY, 0);
    }

    public static void setLastCleanTime(Context context, long value){
        setPreferenceLong(context, LAST_CLEAN_KEY, value);
    }

    public static void setUsageTime(Context context, long value){
        Log.d(TAG, "setUsageTime: ");
        setPreferenceLong(context, USAGE_TIME_KEY, value);
    }

    public static long getUsageTime(Context context) {
        return getPreferenceLong(context, USAGE_TIME_KEY, FULL_USAGE_TIME);
    }


    public static void setStatusBarOpened(Context context, boolean value){
        setPreferenceBoolean(context, STATUS_BAR_KEY, value);
    }

    public static boolean getStatusBarOpened(Context context){
        return getPreferenceBoolean(context, STATUS_BAR_KEY, true);
    }

    public static void setLowBatteryOpened(Context context, boolean value){
        setPreferenceBoolean(context, LOW_BATTERY_KEY, value);
    }

    public static boolean getLowBatteryOpened(Context context){
        return getPreferenceBoolean(context, LOW_BATTERY_KEY, true);
    }

    public static void setFullyChargedOpened(Context context, boolean value){
        setPreferenceBoolean(context, FULLY_CHARGED_KEY, value);
    }

    public static boolean getFullyChargedOpened(Context context){
        return getPreferenceBoolean(context, FULLY_CHARGED_KEY, true);
    }

    public static void setOverChargedOpened(Context context, boolean value){
        setPreferenceBoolean(context, OVER_CHARGED_KEY, value);
    }

    public static boolean getOverChargedOpened(Context context){
        return getPreferenceBoolean(context, OVER_CHARGED_KEY, true);
    }



    public static void setAutoLaunchOpened(Context context, boolean value){
        setPreferenceBoolean(context, AUTO_LAUNCH_KEY, value);
    }

    public static boolean getAutoLaunchOpened(Context context){
        return getPreferenceBoolean(context, AUTO_LAUNCH_KEY, true);
    }

    public static void setLockScreenOpened(Context context, boolean value){
        setPreferenceBoolean(context, LOCK_SCREEN_KEY, value);
    }

    public static boolean getLockScreenOpened(Context context){
        return getPreferenceBoolean(context, LOCK_SCREEN_KEY, true);
    }

    public static long getFullyChargedTime(Context context){
        return getPreferenceLong(context, FULLY_CHARGED_TIME_KEY, 0);
    }

    public static void setFullyChargedTime(Context context, long value){
        setPreferenceLong(context, FULLY_CHARGED_TIME_KEY, value);
    }

    public static void setDeleteCleanTipsNotificationTime(Context context, long value) {
        setPreferenceLong(context, DELETE_CLEAN_TIPS_NOTIFICATION, value);
    }

    public static long getDeleteCleanTipsNotificationTime(Context context) {
        return getPreferenceLong(context, DELETE_CLEAN_TIPS_NOTIFICATION, 0);
    }

    public static void setDeleteLowPowerNotificationTime(Context context, long value) {
        setPreferenceLong(context, DELETE_LOW_POWER_NOTIFICATION, value);
    }

    public static long getDeleteLowPowerNotificationTime(Context context) {
        return getPreferenceLong(context, DELETE_LOW_POWER_NOTIFICATION, 0);
    }

    public static void setDeleteVeryLowPowerNotificationTime(Context context, long value) {
        setPreferenceLong(context, DELETE_VERY_LOW_POWER_NOTIFICATION, value);
    }

    public static long getDeleteVeryLowPowerNotificationTime(Context context) {
        return getPreferenceLong(context, DELETE_VERY_LOW_POWER_NOTIFICATION, 0);
    }


    public static void setDeleteFullyChargedNotificationTime(Context context, long value) {
        setPreferenceLong(context, DELETE_FULLY_CHARGED_NOTIFICATION, value);
    }

    public static long getDeleteFullyChargedNotificationTime(Context context) {
        return getPreferenceLong(context, DELETE_FULLY_CHARGED_NOTIFICATION, 0);
    }

    public static void setDeleteOverChargedNotificationTime(Context context, long value) {
        setPreferenceLong(context, DELETE_OVER_CHARGED_NOTIFICATION, value);
    }

    public static long getDeleteOverChargedNotificationTime(Context context) {
        return getPreferenceLong(context, DELETE_OVER_CHARGED_NOTIFICATION, 0);
    }


    public static boolean getHasCreateShortCut(Context context) {
        return getPreferenceBoolean(context, HAS_CREATE_SHORTCUT, false);
    }

    public static void setHasCreateShortCut(Context context, boolean value) {
         setPreferenceBoolean(context, HAS_CREATE_SHORTCUT, value);
    }

    public static void saveSettings(Context context, MyModeSettings myModeSettings) {
        setMobileDataIsOpened(context, myModeSettings.mMobileDataOpened);
        setWifiIsOpened(context, myModeSettings.mWifiOpened);
        setSyncIsOpened(context, myModeSettings.mSyncOpened);
        setBluetoothIsOpened(context, myModeSettings.mBluetoothOpened);
        setVibrateIsOpened(context, myModeSettings.mVibrateOpened);
        setHapticIsOpened(context, myModeSettings.mHapticOpened);
        setRingerPercent(context, myModeSettings.mRingerPercent);
        setBrightnessIsAuto(context, myModeSettings.mBrightnessAuto);
        setMeidaVolumePercent(context, myModeSettings.mMediaPercent);
        setBrightnessPercent(context, myModeSettings.mBrightnessPercent);
        setScreenOutPercent(context, myModeSettings.mScreenOutPercent);
    }


}
