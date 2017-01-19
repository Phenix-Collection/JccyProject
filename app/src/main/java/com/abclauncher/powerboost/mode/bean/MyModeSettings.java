package com.abclauncher.powerboost.mode.bean;

/**
 * Created by sks on 2016/12/28.
 */

public class MyModeSettings implements Cloneable{
    public boolean mMobileDataOpened, mWifiOpened, mSyncOpened,
            mBluetoothOpened, mVibrateOpened, mHapticOpened, mBrightnessAuto;
    public int mRingerPercent, mMediaPercent, mBrightnessPercent, mScreenOutPercent;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this){
            return true;
        }
        if (o instanceof MyModeSettings) {
            MyModeSettings myModeSettings = (MyModeSettings) o;
            return myModeSettings.mMobileDataOpened == mMobileDataOpened &&
                    myModeSettings.mWifiOpened == mWifiOpened &&
                    myModeSettings.mSyncOpened == mSyncOpened &&
                    myModeSettings.mBluetoothOpened == mBluetoothOpened &&
                    myModeSettings.mVibrateOpened == mVibrateOpened &&
                    myModeSettings.mHapticOpened == mHapticOpened &&
                    myModeSettings.mRingerPercent == mRingerPercent &&
                    myModeSettings.mMediaPercent == mMediaPercent &&
                    myModeSettings.mBrightnessPercent == mBrightnessPercent &&
                    myModeSettings.mScreenOutPercent == mScreenOutPercent &&
                    myModeSettings.mBrightnessAuto == mBrightnessAuto;
        }
        return false;
    }
}