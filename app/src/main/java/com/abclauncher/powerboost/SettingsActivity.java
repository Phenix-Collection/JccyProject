package com.abclauncher.powerboost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.abclauncher.powerboost.notification.CleanNotification;
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
 * Created by sks on 2017/1/6.
 */

public class SettingsActivity extends BaseActivity {

    private AlertDialog mAlertDialog;

    @OnClick(R.id.back)
    public void onBackBtnPressed(){
        onBackPressed();
    }

    @OnClick(R.id.status_bar_item)
    public void onStatusBarItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_STATUS_BAR);
        mStatusBarSwitch.setChecked(!mStatusBarSwitch.isChecked());
        SettingsHelper.setStatusBarOpened(getApplicationContext(), mStatusBarSwitch.isChecked());
        if(mStatusBarSwitch.isChecked()){
            CleanNotification.show(getApplicationContext());
        }else {
            CleanNotification.cancelNotification(getApplicationContext());
        }
    }

    @OnClick(R.id.low_battery_item)
    public void onLowBatteryItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_LOW_BATTERY);
        mLowBatterySwitch.setChecked(!mLowBatterySwitch.isChecked());
        SettingsHelper.setLowBatteryOpened(getApplicationContext(), mLowBatterySwitch.isChecked());
    }

    @OnClick(R.id.fully_charged_item)
    public void onFullyChargedItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_FULLY_CHARGED);
        mFullyChargedSwitch.setChecked(!mFullyChargedSwitch.isChecked());
        SettingsHelper.setFullyChargedOpened(getApplicationContext(), mFullyChargedSwitch.isChecked());
    }

    @OnClick(R.id.over_charged_item)
    public void onOverChargedItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_OVER_CHARGED);
        mOverChargedSwitch.setChecked(!mOverChargedSwitch.isChecked());
        SettingsHelper.setOverChargedOpened(getApplicationContext(), mOverChargedSwitch.isChecked());
    }

    @OnClick(R.id.auto_launch_item)
    public void onAutoLaunchItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_AUTO_LAUNCHE);
        mAutoLaunchSwitch.setChecked(!mAutoLaunchSwitch.isChecked());
        SettingsHelper.setAutoLaunchOpened(getApplicationContext(), mAutoLaunchSwitch.isChecked());
    }

    @OnClick(R.id.lock_screen_item)
    public void onLockScreenItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.SETTINGS_ITEM, StatsUtil.SETTINGS_LOCK_SCREEN);
        if (mLockScreenSwitch.isChecked()) {
            showCloseLockScreenDialog();
        } else {
            mLockScreenSwitch.setChecked(!mLockScreenSwitch.isChecked());
            SettingsHelper.setLockScreenOpened(getApplicationContext(), mLockScreenSwitch.isChecked());
        }
    }

    private void showCloseLockScreenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.settings_close_lock_screen_dialog, null);
        initView(view);
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    private void initView(View view) {

        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
                mLockScreenSwitch.setChecked(!mLockScreenSwitch.isChecked());
                SettingsHelper.setLockScreenOpened(getApplicationContext(), mLockScreenSwitch.isChecked());
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
    }

    @InjectView(R.id.status_bar_switch)
    SwitchCompat mStatusBarSwitch;
    @InjectView(R.id.low_battery_switch)
    SwitchCompat mLowBatterySwitch;
    @InjectView(R.id.fully_charged_switch)
    SwitchCompat mFullyChargedSwitch;
    @InjectView(R.id.over_charged_switch)
    SwitchCompat mOverChargedSwitch;
    @InjectView(R.id.auto_launch_switch)
    SwitchCompat mAutoLaunchSwitch;
    @InjectView(R.id.lock_screen_switch)
    SwitchCompat mLockScreenSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);
        ButterKnife.inject(this);

        initCurrentStatus();
    }

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }


    private void initCurrentStatus() {
        mStatusBarSwitch.setChecked(SettingsHelper.getStatusBarOpened(getApplicationContext()));
        mLowBatterySwitch.setChecked(SettingsHelper.getLowBatteryOpened(getApplicationContext()));
        mFullyChargedSwitch.setChecked(SettingsHelper.getFullyChargedOpened(getApplicationContext()));
        mOverChargedSwitch.setChecked(SettingsHelper.getOverChargedOpened(getApplicationContext()));
        mAutoLaunchSwitch.setChecked(SettingsHelper.getAutoLaunchOpened(getApplicationContext()));
        mLockScreenSwitch.setChecked(SettingsHelper.getLockScreenOpened(getApplicationContext()));
    }
}
