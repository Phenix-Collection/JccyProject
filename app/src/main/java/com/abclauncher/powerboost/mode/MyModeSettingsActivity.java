package com.abclauncher.powerboost.mode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abclauncher.powerboost.BaseActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.mode.bean.MyModeSettings;
import com.abclauncher.powerboost.util.AnalyticsHelper;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.StatsUtil;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.abclauncher.powerboost.util.SettingsHelper.MODE_BATTERY;
import static com.abclauncher.powerboost.util.SettingsHelper.MODE_GENERAL;
import static com.abclauncher.powerboost.util.SettingsHelper.MODE_SLEEP;

/**
 * Created by sks on 2016/12/28.
 */

public class MyModeSettingsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "ModeActivity";
    private MyModeSettings mMyModeSettingsOld;
    private MyModeSettings myModeSettingsNew;
    private boolean mHasShowWarnDialog = false;
    private boolean mClickedSave;
    private AlertDialog mAlertDialog;

    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @InjectView(R.id.mobile_data_item)
    View mMobileDataItem;
    @OnClick(R.id.mobile_data_item)
    public void onMobileDataItemClicked(){
        mMobileDataSwitch.setChecked(!mMobileDataSwitch.isChecked());
        myModeSettingsNew.mMobileDataOpened = mMobileDataSwitch.isChecked();
    }

    @OnClick(R.id.my_mode_wifi_item)
    public void onWifiItemClicked(){
        mWifiSwitch.setChecked(!mWifiSwitch.isChecked());
        myModeSettingsNew.mWifiOpened = mWifiSwitch.isChecked();
    }

    @OnClick(R.id.my_mode_synchronous_item)
    public void onSyncItemClicked(){
        mSynchronousSwitch.setChecked(!mSynchronousSwitch.isChecked());
        myModeSettingsNew.mSyncOpened = mSynchronousSwitch.isChecked();
    }

    @OnClick(R.id.my_mode_bluetooth_item)
    public void onBluetoothItemClicked(){
        mBluetoothSwitch.setChecked(!mBluetoothSwitch.isChecked());
        myModeSettingsNew.mBluetoothOpened = mBluetoothSwitch.isChecked();
    }

    @OnClick(R.id.my_mode_vibrate_item)
    public void onVibrateItemClicked(){
        mVibrateSwitch.setChecked(!mVibrateSwitch.isChecked());
        myModeSettingsNew.mVibrateOpened = mVibrateSwitch.isChecked();
    }

    @OnClick(R.id.my_mode_haptic_item)
    public void onHapticItemClicked(){
        mHapticSwitch.setChecked(!mHapticSwitch.isChecked());
        myModeSettingsNew.mHapticOpened = mHapticSwitch.isChecked();
    }
    @OnClick(R.id.brightness_auto)
    public void onBrightnessAotuClicked(){
        if (mSeekbarBrightness.isEnabled()) {
            mBrightAuto.setImageResource(R.drawable.radio_btn_active);
            mSeekbarBrightness.setEnabled(false);
            myModeSettingsNew.mBrightnessAuto = true;
        }else {
            mBrightAuto.setImageResource(R.drawable.radio_btn_unactive);
            mSeekbarBrightness.setEnabled(true);
            myModeSettingsNew.mBrightnessAuto = false;
        }
    }

    @OnClick(R.id.save)
    public void onSaveBtnClicked(){
        mClickedSave = true;
        if (mClickedSave && myModeSettingsNew != null && !myModeSettingsNew.equals(mMyModeSettingsOld)){
            Intent intent = getIntent();
            if (myModeSettingsNew.mVibrateOpened != mMyModeSettingsOld.mVibrateOpened) {
                //intent.getBooleanArrayExtra(ModeActivity.STATUS_VIBRATE_CHANGED, )
            }
            SettingsHelper.saveSettings(getApplicationContext(), myModeSettingsNew);
            setResult(RESULT_OK, intent);
            Log.d(TAG, "finish: setResult");
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @InjectView(R.id.mobile_data_switch)
    SwitchCompat mMobileDataSwitch;
    @InjectView(R.id.wifi_switch)
    SwitchCompat mWifiSwitch;
    @InjectView(R.id.synchronous_switch)
    SwitchCompat mSynchronousSwitch;
    @InjectView(R.id.bluetooth_switch)
    SwitchCompat mBluetoothSwitch;
    @InjectView(R.id.vibrate_switch)
    SwitchCompat mVibrateSwitch;
    @InjectView(R.id.haptic_feedback_switch)
    SwitchCompat mHapticSwitch;

    @InjectView(R.id.my_mode_ringer_value)
    TextView mRingerValue;
    @InjectView(R.id.my_mode_media_volume_value)
    TextView mMediaVolumeValue;
    @InjectView(R.id.my_mode_brightness_value)
    TextView mBrightnessValue;
    @InjectView(R.id.my_mode_screen_out_value)
    TextView mScreenOutValue;

    @InjectView(R.id.ringer_seek_bar)
    SeekBar mSeekbarRinger;
    @InjectView(R.id.media_volume_seek_bar)
    SeekBar mSeekbarMeidaVolume;
    @InjectView(R.id.brightness_seek_bar)
    SeekBar mSeekbarBrightness;
    @InjectView(R.id.screen_out_seek_bar)
    SeekBar mSeekbarScreenOut;

    @InjectView(R.id.brightness_auto)
    ImageView mBrightAuto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_mode);
        ButterKnife.inject(this);

        mSeekbarBrightness.setOnSeekBarChangeListener(this);
        mSeekbarMeidaVolume.setOnSeekBarChangeListener(this);
        mSeekbarRinger.setOnSeekBarChangeListener(this);
        mSeekbarScreenOut.setOnSeekBarChangeListener(this);
        initStatus();
    }

    private void initStatus() {
        initMyModeSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //5.0 以上 不显示 数据流量开关
            mMobileDataItem.setVisibility(View.GONE);
        }
        mMobileDataSwitch.setChecked(mMyModeSettingsOld.mMobileDataOpened);
        mWifiSwitch.setChecked(mMyModeSettingsOld.mWifiOpened);
        mSynchronousSwitch.setChecked(mMyModeSettingsOld.mSyncOpened);
        mBluetoothSwitch.setChecked(mMyModeSettingsOld.mBluetoothOpened);
        mVibrateSwitch.setChecked(mMyModeSettingsOld.mVibrateOpened);
        mHapticSwitch.setChecked(mMyModeSettingsOld.mHapticOpened);
        if (mMyModeSettingsOld.mRingerPercent == 0) {
            mRingerValue.setText(R.string.ringer_silent);
        }else{
            mRingerValue.setText(mMyModeSettingsOld.mRingerPercent + "%");
        }
        mMediaVolumeValue.setText(mMyModeSettingsOld.mMediaPercent + "%");
        mBrightnessValue.setText(mMyModeSettingsOld.mBrightnessPercent + "%");
        mScreenOutValue.setText(getResources().getStringArray(R.array.screen_out_time_values)[mMyModeSettingsOld.mScreenOutPercent]);
        if (mMyModeSettingsOld.mBrightnessAuto) {
            mBrightAuto.setImageResource(R.drawable.radio_btn_active);
            mSeekbarBrightness.setEnabled(false);
        }else {
            mBrightAuto.setImageResource(R.drawable.radio_btn_unactive);
            mSeekbarBrightness.setEnabled(true);
        }

        mSeekbarRinger.setProgress(mMyModeSettingsOld.mRingerPercent/10);
        mSeekbarMeidaVolume.setProgress(mMyModeSettingsOld.mMediaPercent/10);
        mSeekbarBrightness.setProgress((mMyModeSettingsOld.mBrightnessPercent/10) - 1);
        mSeekbarScreenOut.setProgress(mMyModeSettingsOld.mScreenOutPercent);
    }

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }
    public void initMyModeSettings(){
        mMyModeSettingsOld = new MyModeSettings();
        mMyModeSettingsOld.mMobileDataOpened = SettingsHelper.getMobileDataIsOpened(getApplicationContext());
        mMyModeSettingsOld.mWifiOpened = SettingsHelper.getWifiIsOpened(getApplicationContext());
        mMyModeSettingsOld.mSyncOpened = SettingsHelper.getSyncIsOpened(getApplicationContext());
        mMyModeSettingsOld.mBluetoothOpened = SettingsHelper.getBluetoothIsOpened(getApplicationContext());
        mMyModeSettingsOld.mVibrateOpened = SettingsHelper.getVibrateIsOpened(getApplicationContext());
        mMyModeSettingsOld.mHapticOpened = SettingsHelper.getHapticIsOpened(getApplicationContext());
        mMyModeSettingsOld.mBrightnessAuto = SettingsHelper.getBrightnessIsAuto(getApplicationContext());
        mMyModeSettingsOld.mRingerPercent = SettingsHelper.getRingerPercent(getApplicationContext());
        mMyModeSettingsOld.mMediaPercent = SettingsHelper.getMediaVolumePercent(getApplicationContext());
        mMyModeSettingsOld.mBrightnessPercent = SettingsHelper.getBrighnessPercent(getApplicationContext());
        mMyModeSettingsOld.mScreenOutPercent = SettingsHelper.getScreenOutPercent(getApplicationContext());

        try {
            myModeSettingsNew = (MyModeSettings)mMyModeSettingsOld.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mHasShowWarnDialog && myModeSettingsNew != null && !mMyModeSettingsOld.equals(myModeSettingsNew)){
            mHasShowWarnDialog = true;
            showWarnDialog();
        }else {
            super.onBackPressed();
        }
    }

    private void showWarnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.mode_settings_dialog_layout, null);
        initView(view);
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }

    private void initView(View view) {
        TextView msg = (TextView) view.findViewById(R.id.message);
        msg.setText(R.string.my_mode_settings_warn_des);
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveBtnClicked();
                finish();
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.ringer_seek_bar:
                if (i == 0) {
                    mRingerValue.setText(R.string.ringer_silent);
                }else {
                    mRingerValue.setText(i * 10 + "%");

                }
                myModeSettingsNew.mRingerPercent = i * 10;
                break;
            case R.id.media_volume_seek_bar:
                mMediaVolumeValue.setText(i * 10 + "%");
                myModeSettingsNew.mMediaPercent = i * 10;
                break;
            case R.id.brightness_seek_bar:
                i ++;
                mBrightnessValue.setText(i * 10 + "%");
                myModeSettingsNew.mBrightnessPercent = i * 10;
                break;
            case R.id.screen_out_seek_bar:
                mScreenOutValue.setText(getResources().getStringArray(R.array.screen_out_time_values)[i]);
                myModeSettingsNew.mScreenOutPercent = i;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
