package com.abclauncher.powerboost.mode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abclauncher.powerboost.BaseActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.mode.util.AudioUtil;
import com.abclauncher.powerboost.mode.util.BluetoothUtil;
import com.abclauncher.powerboost.mode.util.BrightnessUtil;
import com.abclauncher.powerboost.mode.util.HapticFeedbackUtil;
import com.abclauncher.powerboost.mode.util.MobileDataUtil;
import com.abclauncher.powerboost.mode.util.ScreenOutUtil;
import com.abclauncher.powerboost.mode.util.SyncUtil;
import com.abclauncher.powerboost.mode.util.WifiUtil;
import com.abclauncher.powerboost.util.AnalyticsHelper;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.StatsUtil;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.abclauncher.powerboost.util.SettingsHelper.MODE_BATTERY;
import static com.abclauncher.powerboost.util.SettingsHelper.MODE_GENERAL;
import static com.abclauncher.powerboost.util.SettingsHelper.MODE_MY;
import static com.abclauncher.powerboost.util.SettingsHelper.MODE_SLEEP;

/**
 * Created by sks on 2016/12/26.
 */

public class ModeActivity extends BaseActivity {
    private static final String TAG = "ModeActivity";
    private static final int REQUEST_CODE = 2;
    public static String STATUS_VIBRATE_CHANGED = "status_vibrate_changed";
    public static final int s15 = 15 * 1000;
    public static final int s30 = 30 * 1000;
    public static final int m1 = 60 * 1000;
    public static final int m5 = 5 * 60 * 1000;
    public static final int m10 = 10 * 60 * 1000;

    private Handler mHander = new Handler();

    @InjectView(R.id.battery_mode_check)
    ImageView mBatteryMode;
    @InjectView(R.id.general_mode_check)
    ImageView mGeneralMode;
    @InjectView(R.id.sleep_mode_check)
    ImageView mSleepMode;
    @InjectView(R.id.my_mode_check)
    ImageView mMyMode;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    private TextView mTitle;

    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @OnClick(R.id.general_mode_menu)
    public void onGeneralModeMenuClicked(){
        showModeDialog(MODE_GENERAL);
    }


    @OnClick(R.id.general_mode)
    public void onGeneralModeItemClicked(){
        if (mCurrentSelectedMode != MODE_GENERAL){
            mCurrentSelectedMode = MODE_GENERAL;
            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_GENERAL_MODE);
            SettingsHelper.setCurrentMode(getApplicationContext(), mCurrentSelectedMode);
            mHander.postDelayed(mShowProgressDialog, 100);
        }
    }

    @OnClick(R.id.battery_save_mode_menu)
    public void onBatterySavingModeMenuClicked(){
        showModeDialog(MODE_BATTERY);
    }
    @OnClick(R.id.battery_save_mode)
    public void onBatterySavingModeItemClicked(){
        if (mCurrentSelectedMode != MODE_BATTERY){
            mCurrentSelectedMode = MODE_BATTERY;
            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_BATTERY_SAVING_MODE);
            SettingsHelper.setCurrentMode(getApplicationContext(), mCurrentSelectedMode);
            mHander.postDelayed(mShowProgressDialog, 100);
        }
    }

    @OnClick(R.id.sleep_mode_menu)
    public void onSleepModeMenuClicked(){
        showModeDialog(MODE_SLEEP);
    }
    @OnClick(R.id.sleep_mode)
    public void onSleepModeItemClicked(){
        if (mCurrentSelectedMode != MODE_SLEEP){
            mCurrentSelectedMode = MODE_SLEEP;
            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_SLEEP_MODE);
            SettingsHelper.setCurrentMode(getApplicationContext(), mCurrentSelectedMode);
            mHander.postDelayed(mShowProgressDialog, 100);
        }
    }

    @OnClick(R.id.my_mode_menu)
    public void onMyModeMenuClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_MY_MODE);
        Intent intent = new Intent(getApplicationContext(), MyModeSettingsActivity.class);
        //startActivityForResult 一定切记不要用这个flag 否则收不到result
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: RESULT_OK");
            if (mCurrentSelectedMode == MODE_MY) {
                applySettingsMyMode();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.my_mode)
    public void onMyModeItemClicked(){
        if (mCurrentSelectedMode != MODE_MY){
            mCurrentSelectedMode = MODE_MY;
            SettingsHelper.setCurrentMode(getApplicationContext(), mCurrentSelectedMode);
            mHander.postDelayed(mShowProgressDialog, 100);
        }
    }


    private int mCurrentSelectedMode;
    private ImageView[] views = new ImageView[4];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_layout);
        ButterKnife.inject(this);
        views[0] = mGeneralMode;
        views[1] = mBatteryMode;
        views[2] = mSleepMode;
        views[3] = mMyMode;

        mCurrentSelectedMode = SettingsHelper.getCurrentMode(getApplicationContext());
        initCurrentStatus(mCurrentSelectedMode);
    }

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    private void initCurrentStatus(int position) {
        for (int i = 0; i < views.length; i++) {
            if (i == position) {
                views[i].setImageResource(R.drawable.radio_btn_active);
            }else {
                views[i].setImageResource(R.drawable.radio_btn_unactive);
            }
        }
    }

    private Runnable mEndSetting = new Runnable(){

        @Override
        public void run() {
            mProgressDialog.dismiss();
            initCurrentStatus(mCurrentSelectedMode);
        }
    };
    private Runnable mShowProgressDialog = new Runnable() {
        @Override
        public void run() {
            showProgressDialog();
            mHander.postDelayed(mStartSettings, 800);
        }
    };
    private Runnable mStartSettings = new Runnable() {
        @Override
        public void run() {
            applySettingsByMode(mCurrentSelectedMode);
            mHander.postDelayed(mEndSetting, 2500);
        }
    };



    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.switching));
        mProgressDialog.show();
    }

    TextView mBrightnessValue;
    TextView mBrightness;
    TextView mScreenTimeOutValue, mScreenTimeOut;
    TextView mWifiValue, mWifi;
    TextView mBlueToothValue, mBlueTooth;
    TextView mMobileDataValue, mMobileData;
    TextView mHaptic, mHapticValue;
    TextView mRinger, mRingerValue;
    TextView mSync, mSyncValue;
    View mRingerItem, mMobileDataItem;
    View mVibrateItem;
    TextView mVibrateValue, mVibrate;

    private void showModeDialog(int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_general_mode, null);
        initView(view, mode);
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void initView(View view, final int mode) {
        mBrightnessValue = (TextView) view.findViewById(R.id.brightness_value);
        mBrightness = (TextView) view.findViewById(R.id.brightness);
        mTitle = (TextView) view.findViewById(R.id.title);
        mScreenTimeOutValue = (TextView) view.findViewById(R.id.screen_out_value);
        mScreenTimeOut = (TextView) view.findViewById(R.id.screen_out);

        mWifiValue = (TextView) view.findViewById(R.id.wifi_value);
        mWifi = (TextView) view.findViewById(R.id.wifi);


        mBlueToothValue = (TextView) view.findViewById(R.id.bluetooth_value);
        mBlueTooth = (TextView) view.findViewById(R.id.bluetooth);

        mSync = (TextView) view.findViewById(R.id.synchronous);
        mSyncValue = (TextView) view.findViewById(R.id.synchronous_value);

        mHaptic = (TextView) view.findViewById(R.id.haptic_feedback);
        mHapticValue = (TextView) view.findViewById(R.id.haptic_feedback_value);

        mRinger = (TextView) view.findViewById(R.id.ringer);
        mRingerValue = (TextView) view.findViewById(R.id.ringer_value);

        mMobileDataValue = (TextView) view.findViewById(R.id.mobile_data_value);
        mMobileDataItem = view.findViewById(R.id.mobile_data_item);
        mMobileData = (TextView) view.findViewById(R.id.mobile_data);
        mRingerItem = view.findViewById(R.id.ringer_item);
        mVibrateItem = view.findViewById(R.id.vibrate_item);
        mVibrateValue = (TextView) view.findViewById(R.id.vibrate_value);
        mVibrate = (TextView) view.findViewById(R.id.vibrate);
        int redColor = getResources().getColor(R.color.red_end_color);
        switch (mode){
            case MODE_GENERAL:
                mTitle.setText(R.string.general_mode);
                mBrightnessValue.setText(R.string.brightness_value_auto);
                mScreenTimeOutValue.setText(getResources().getStringArray(R.array.screen_out_time_values)[2]);
                mWifiValue.setText(R.string.on);
                mMobileDataValue.setText(R.string.on);
                mRingerItem.setVisibility(View.GONE);
                mVibrateItem.setVisibility(View.GONE);

                //不同于 系统当前开关的 设置成红色

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //5.0 以上 不显示 数据流量开关
                    mMobileDataItem.setVisibility(View.GONE);
                } else {
                    if (!MobileDataUtil.getInstance(getApplicationContext()).getMobileDataState()){
                        mMobileDataValue.setTextColor(redColor);
                        mMobileData.setTextColor(redColor);
                    }
                }


                if (!BrightnessUtil.getInstance(getApplicationContext()).isAutoMode()) {
                    mBrightnessValue.setTextColor(redColor);
                    mBrightness.setTextColor(redColor);
                }

                if (ScreenOutUtil.getInstance(getApplicationContext()).getScreenOutTime() != m1) {
                    mScreenTimeOutValue.setTextColor(redColor);
                    mScreenTimeOut.setTextColor(redColor);
                }

                if (!WifiUtil.getInstance(getApplicationContext()).getWifiOpened()){
                    mWifiValue.setTextColor(redColor);
                    mWifi.setTextColor(redColor);
                }

                if (BluetoothUtil.getInstance(getApplicationContext()).getBluetoothStatus()){
                    mBlueTooth.setTextColor(redColor);
                    mBlueToothValue.setTextColor(redColor);
                }

                if (SyncUtil.getInstance(getApplicationContext()).getSyncStatus()){
                    mSync.setTextColor(redColor);
                    mSyncValue.setTextColor(redColor);
                }

                if (HapticFeedbackUtil.getInstance(getApplicationContext()).getHapticFeedbackEnable()){
                    mHaptic.setTextColor(redColor);
                    mHapticValue.setTextColor(redColor);
                }

                break;
            case MODE_BATTERY:
                mTitle.setText(R.string.battery_save_mode);
                mBrightnessValue.setText(R.string.brightness_value_low);
                mScreenTimeOutValue.setText(getResources().getStringArray(R.array.screen_out_time_values)[0]);
                mWifiValue.setText(R.string.off);
                mMobileDataValue.setText(R.string.off);
                mVibrateValue.setText(R.string.on);
                mRingerItem.setVisibility(View.GONE);


                //不同于 系统当前开关的 设置成红色
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //5.0 以上 不显示 数据流量开关
                    mMobileDataItem.setVisibility(View.GONE);
                } else {
                    if (MobileDataUtil.getInstance(getApplicationContext()).getMobileDataState()){
                        mMobileDataValue.setTextColor(redColor);
                        mMobileData.setTextColor(redColor);
                    }
                }


                if (BrightnessUtil.getInstance(getApplicationContext()).getCurBrightnessPercent() != 10) {
                    mBrightnessValue.setTextColor(redColor);
                    mBrightness.setTextColor(redColor);
                }

                if (ScreenOutUtil.getInstance(getApplicationContext()).getScreenOutTime() != s15) {
                    mScreenTimeOutValue.setTextColor(redColor);
                    mScreenTimeOut.setTextColor(redColor);
                }

                /*if (!AudioUtil.getInstance(getApplicationContext()).isSilent()){
                    mRinger.setTextColor(redColor);
                    mRingerValue.setTextColor(redColor);
                }*/

                if (!AudioUtil.getInstance(getApplicationContext()).isVibrate()){
                    mVibrateValue.setTextColor(redColor);
                    mVibrate.setTextColor(redColor);
                }

                if (WifiUtil.getInstance(getApplicationContext()).getWifiOpened()){
                    mWifiValue.setTextColor(redColor);
                    mWifi.setTextColor(redColor);
                }

                if (BluetoothUtil.getInstance(getApplicationContext()).getBluetoothStatus()){
                    mBlueTooth.setTextColor(redColor);
                    mBlueToothValue.setTextColor(redColor);
                }

                if (SyncUtil.getInstance(getApplicationContext()).getSyncStatus()){
                    mSync.setTextColor(redColor);
                    mSyncValue.setTextColor(redColor);
                }

                if (HapticFeedbackUtil.getInstance(getApplicationContext()).getHapticFeedbackEnable()){
                    mHaptic.setTextColor(redColor);
                    mHapticValue.setTextColor(redColor);
                }
                break;
            case MODE_SLEEP:
                mTitle.setText(R.string.sleep_mode);
                mBrightnessValue.setText(R.string.brightness_value_low);
                mScreenTimeOutValue.setText(getResources().getStringArray(R.array.screen_out_time_values)[0]);
                mWifiValue.setText(R.string.off);
                mMobileDataValue.setText(R.string.off);
                mVibrateValue.setText(R.string.off);

                //不同于 系统当前开关的 设置成红色
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    //5.0 以上 不显示 数据流量开关
                    mMobileDataItem.setVisibility(View.GONE);
                } else {
                    if (MobileDataUtil.getInstance(getApplicationContext()).getMobileDataState()){
                        mMobileDataValue.setTextColor(redColor);
                        mMobileData.setTextColor(redColor);
                    }
                }


                if (BrightnessUtil.getInstance(getApplicationContext()).getCurBrightnessPercent() != 10) {
                    mBrightnessValue.setTextColor(redColor);
                    mBrightness.setTextColor(redColor);
                }

                if (ScreenOutUtil.getInstance(getApplicationContext()).getScreenOutTime() != s15) {
                    mScreenTimeOutValue.setTextColor(redColor);
                    mScreenTimeOut.setTextColor(redColor);
                }

                if (!AudioUtil.getInstance(getApplicationContext()).isSilent()){
                    mRinger.setTextColor(redColor);
                    mRingerValue.setTextColor(redColor);
                }

                if (AudioUtil.getInstance(getApplicationContext()).isVibrate()){
                    mVibrateValue.setTextColor(redColor);
                    mVibrate.setTextColor(redColor);
                }

                if (WifiUtil.getInstance(getApplicationContext()).getWifiOpened()){
                    mWifiValue.setTextColor(redColor);
                    mWifi.setTextColor(redColor);
                }

                if (BluetoothUtil.getInstance(getApplicationContext()).getBluetoothStatus()){
                    mBlueTooth.setTextColor(redColor);
                    mBlueToothValue.setTextColor(redColor);
                }

                if (SyncUtil.getInstance(getApplicationContext()).getSyncStatus()){
                    mSync.setTextColor(redColor);
                    mSyncValue.setTextColor(redColor);
                }

                if (HapticFeedbackUtil.getInstance(getApplicationContext()).getHapticFeedbackEnable()){
                    mHaptic.setTextColor(redColor);
                    mHapticValue.setTextColor(redColor);
                }
                break;
        }

        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
                if (mCurrentSelectedMode != mode) {
                    mCurrentSelectedMode = mode;
                    switch (mode) {
                        case MODE_GENERAL:
                            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_GENERAL_MODE);
                            break;
                        case MODE_BATTERY:
                            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_BATTERY_SAVING_MODE);
                            break;
                        case MODE_SLEEP:
                            AnalyticsHelper.sendEvent(StatsUtil.MODE_ITEM, StatsUtil.MODE_ITEM_SLEEP_MODE);
                            break;
                    }
                    SettingsHelper.setCurrentMode(getApplicationContext(), mode);
                    mHander.postDelayed(mShowProgressDialog, 100);
                }

            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
    }

    private void applySettingsByMode(int mode) {
        switch (mode){
            case MODE_GENERAL:
                applySettingsGeneralMode();
                break;
            case MODE_BATTERY:
                applySettingsBatteryMode();
                break;
            case MODE_SLEEP:
                applySettingsSleepMode();
                break;
            case MODE_MY:
                applySettingsMyMode();
        }


    }

    private void applySettingsMyMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //5.0 以上 不显示 数据流量开关
            MobileDataUtil.getInstance(getApplicationContext()).setMobileData(SettingsHelper.getMobileDataIsOpened(getApplicationContext()));
        }
        if (SettingsHelper.getWifiIsOpened(getApplicationContext())){
            WifiUtil.getInstance(getApplicationContext()).openWifi();
        }else {
            WifiUtil.getInstance(getApplicationContext()).closeWifi();
        }

        SyncUtil.getInstance(getApplicationContext()).setSyncStatus(SettingsHelper.getSyncIsOpened(getApplicationContext()));
        BluetoothUtil.getInstance(getApplicationContext()).setBluetooth(SettingsHelper.getBluetoothIsOpened(getApplicationContext()));
        if (SettingsHelper.getVibrateIsOpened(getApplicationContext())) {
            AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_VIBRATE);
        }

        if (SettingsHelper.getRingerPercent(getApplicationContext()) != 0){
            AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_NORMAL);
            AudioUtil.getInstance(getApplicationContext()).setRingerVolume(SettingsHelper.getRingerPercent(getApplicationContext()));
        }else if (!SettingsHelper.getVibrateIsOpened(getApplicationContext())){
            AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_SILENT);
        }

        HapticFeedbackUtil.getInstance(getApplicationContext()).setHapticFeedbackEnable(SettingsHelper.getHapticIsOpened(getApplicationContext()));

        AudioUtil.getInstance(getApplicationContext()).setMediaVolume(SettingsHelper.getMediaVolumePercent(getApplicationContext()));

        if (SettingsHelper.getBrightnessIsAuto(getApplicationContext())) {
            BrightnessUtil.getInstance(getApplicationContext()).setCurBrightnessMode(BrightnessUtil.AUTO_BRIGHTNESS);
        }else {
            BrightnessUtil.getInstance(getApplicationContext()).setCurBrightnessMode(BrightnessUtil.MANUAL_BRIGHTNESS);
            BrightnessUtil.getInstance(getApplicationContext()).setBrightnessPercent(SettingsHelper.getBrighnessPercent(getApplicationContext()));
        }

        int screenOut = ScreenOutUtil.getInstance(getApplicationContext()).getScreenTimeOutByPercent(SettingsHelper.getScreenOutPercent(getApplicationContext()));
        ScreenOutUtil.getInstance(getApplicationContext()).setScreenOutTime(screenOut);
    }

    private void applySettingsSleepMode() {
        BrightnessUtil.getInstance(getApplicationContext()).setCurBrightnessMode(BrightnessUtil.MANUAL_BRIGHTNESS);
        BrightnessUtil.getInstance(getApplicationContext()).setBrightnessPercent(10);
        ScreenOutUtil.getInstance(getApplicationContext()).setScreenOutTime(ScreenOutUtil.s15);
        WifiUtil.getInstance(getApplicationContext()).closeWifi();
        BluetoothUtil.getInstance(getApplicationContext()).setBluetooth(false);
        MobileDataUtil.getInstance(getApplicationContext()).setMobileData(false);
        SyncUtil.getInstance(getApplicationContext()).setSyncStatus(false);
        HapticFeedbackUtil.getInstance(getApplicationContext()).setHapticFeedbackEnable(false);
        //AudioUtil.getInstance(getApplicationContext()).setRingerVolume(0);
        AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void applySettingsBatteryMode() {
        BrightnessUtil.getInstance(getApplicationContext()).setCurBrightnessMode(BrightnessUtil.MANUAL_BRIGHTNESS);
        BrightnessUtil.getInstance(getApplicationContext()).setBrightnessPercent(10);
        ScreenOutUtil.getInstance(getApplicationContext()).setScreenOutTime(ScreenOutUtil.s15);
        WifiUtil.getInstance(getApplicationContext()).closeWifi();
        BluetoothUtil.getInstance(getApplicationContext()).setBluetooth(false);
        MobileDataUtil.getInstance(getApplicationContext()).setMobileData(false);
        SyncUtil.getInstance(getApplicationContext()).setSyncStatus(false);
        HapticFeedbackUtil.getInstance(getApplicationContext()).setHapticFeedbackEnable(false);
        AudioUtil.getInstance(getApplicationContext()).setRingerVolume(0);
        AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    private void applySettingsGeneralMode() {
        BrightnessUtil.getInstance(getApplicationContext()).setBrightnessPercent(55);
        BrightnessUtil.getInstance(getApplicationContext()).setCurBrightnessMode(BrightnessUtil.AUTO_BRIGHTNESS);
        AudioUtil.getInstance(getApplicationContext()).setRingMode(AudioManager.RINGER_MODE_NORMAL);
        AudioUtil.getInstance(getApplicationContext()).setRingerVolume(100);
        ScreenOutUtil.getInstance(getApplicationContext()).setScreenOutTime(ScreenOutUtil.m1);
        WifiUtil.getInstance(getApplicationContext()).openWifi();
        BluetoothUtil.getInstance(getApplicationContext()).setBluetooth(false);
        MobileDataUtil.getInstance(getApplicationContext()).setMobileData(true);
        SyncUtil.getInstance(getApplicationContext()).setSyncStatus(false);
        HapticFeedbackUtil.getInstance(getApplicationContext()).setHapticFeedbackEnable(false);
    }


}
