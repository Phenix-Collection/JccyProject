package com.abclauncher.powerboost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abclauncher.powerboost.bean.FacebookNativeAdBean;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.receiver.BatteryDataReceiver;
import com.abclauncher.powerboost.util.AdUtil;
import com.abclauncher.powerboost.util.FacebookAdCallbackDtail;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.Utils;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;
import com.abclauncher.powerboost.view.BatteryProgress;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.NativeAd;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sks on 2017/1/3.
 */

public class ChargeActivity extends BaseActivity implements BatteryDataReceiver.BatteryCallback{

    private static final String TAG = "ChargeActivity";
    private static final int REFRESH_BATTERY = 4;
    private BatteryDataReceiver mBatteryReceiver;
    private static  final int STATUS_NONE= -1;
    private static  final int STATUS_SPEED = 0;
    private static  final int STATUS_CONTINUOUS = 1;
    private static  final int STATUS_TRICKLE = 2;
    private static final long FULLY_CHARGED_TIME_THRESHOLD = 1000 * 60 * 10;

    @InjectView(R.id.tv_battery_progress)
    TextView mTvBatteryProgress;
    @InjectView(R.id.ic_speed)
    ImageView mSpeed;
    @InjectView(R.id.ic_speed_container)
    ImageView mSpeedContainer;
    @InjectView(R.id.ic_speed_progress)
    ImageView mSpeedProgress;
    @InjectView(R.id.ic_continuous)
    ImageView mContinuous;
    @InjectView(R.id.ic_continuous_container)
    ImageView mContinuousContainer;
    @InjectView(R.id.ic_continuous_progress)
    ImageView mContinuousProgress;
    @InjectView(R.id.ic_trickle)
    ImageView mTrickle;
    @InjectView(R.id.ic_trickle_container)
    ImageView mTrickleContainer;
    @InjectView(R.id.ic_trickle_progress)
    ImageView mTrickleProgress;

    @InjectView(R.id.link_one)
    View mLinkOne;
    @InjectView(R.id.link_two)
    View mLinkTwo;

    @InjectView(R.id.tv_speed)
    TextView mTvSpeed;
    @InjectView(R.id.tv_continuous)
    TextView mTvContinuous;
    @InjectView(R.id.tv_trickle)
    TextView mTvTrickle;

    @InjectView(R.id.battery_container)
    FrameLayout mBatteryContainer;
    @InjectView(R.id.battery_des)
    LinearLayout mBatteryDes;
    @InjectView(R.id.battery_status)
    FrameLayout mBatteryStatus;
    @InjectView(R.id.ad_layout)
    LinearLayout mAdLayout;
    @InjectView(R.id.battery_percent)
    TextView mBatteryPercent;

    //ad view
    @InjectView(R.id.native_ad_image)
    ImageView mAdCoverIv;
    @InjectView(R.id.native_ad_icon)
    ImageView mAdIconIv;
    @InjectView(R.id.native_ad_title)
    TextView mAdTitleIv;
    @InjectView(R.id.native_ad_body)
    TextView mAdBodyTv;
    @InjectView(R.id.native_ad_call_to_action)
    TextView mAdInstallTv;

    @InjectView(R.id.time_left)
    TextView mTimeLeftDes;
    @InjectView(R.id.usage_time_hour_value)
    TextView mTimeLeftHourValue;
    @InjectView(R.id.usage_time_minutes_value)
    TextView mTimeLeftMinutesValue;
    @InjectView(R.id.charge_value)
    View mChargeValue;

    @InjectView(R.id.bp_battery_progress)
    BatteryProgress mBpBatteryProgress;
    private boolean mIsCharging;
    private int mCurrentStatus = STATUS_NONE;
    private ValueAnimator mRotateAnimator;

    private float mBatteryContainerTranslateX;
    private float mBatteryContainerTranslateY;
    int[] startPointOne = new int[2];
    int[] startPointTwo = new int[2];
    int[] startPointThree = new int[2];
    private float  mBatteryContainerScaleTo = 0.8f;
    private float  mBatteryDesScaleTo = 0.75f;
    private float mBatteryDesTranslateX, mBatteryDesTranslateY;
    private float mBatteryStatusTranslateY;
    private boolean mAdIsLoaded = false;
    private NativeAd nativeAd;
    private boolean mShowUsageTime;
    private int mPercent;
    private boolean mShowTrickleTime;
    private boolean mHasAnim = false;

    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_layout);
        ButterKnife.inject(this);
        registerReceiver();


        initAd();

        mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 0);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }


    private void initAd(){
        AdUtil.loadNativeAd(this, AdUtil.AD_CHARGE_UI, new FacebookAdCallbackDtail() {
            @Override
            public void onNativeAdLoadError() {
            }

            @Override
            public void onNativeAdClick(Ad ad) {
                ad.destroy();
            }

            @Override
            public void onNativeAdLoaded(FacebookNativeAdBean facebookNativeAdBean) {
                if (!mHasAnim) {
                    mHasAnim = true;
                    mHandler.postDelayed(run, 0);
                }
                String textForAdTitle = facebookNativeAdBean.title;
                String coverImgUrl = facebookNativeAdBean.coverImgUrl;
                String iconForAdUrl = facebookNativeAdBean.iconForAdUrl;
                String textForAdBody = facebookNativeAdBean.textForAdBody;
                String adAction = facebookNativeAdBean.actionBtnText;
                nativeAd = facebookNativeAdBean.nativeAd;
                String contextString = nativeAd.getAdSocialContext();

                mAdTitleIv.setText(textForAdTitle);
                if (TextUtils.isEmpty(adAction)) {
                    mAdInstallTv.setVisibility(View.GONE);
                } else {
                    mAdInstallTv.setText(adAction);
                }
                mAdBodyTv.setText(textForAdBody);
                Glide.with(getApplicationContext())
                        .load(coverImgUrl)
                        .into(mAdCoverIv);
                Glide.with(getApplicationContext())
                        .load(iconForAdUrl)
                        .into(mAdIconIv);
                facebookNativeAdBean.nativeAd.registerViewForInteraction(mAdLayout);
            }
        });
    }

    private void calculateTranslate() {
        Log.d(TAG, "calculateTranslate: ");
        mBatteryContainer.getLocationOnScreen(startPointOne);
        mBatteryDes.getLocationOnScreen(startPointTwo);
        mBatteryDes.getLocationOnScreen(startPointTwo);
        mBatteryStatus.getLocationOnScreen(startPointThree);
        mBatteryContainerTranslateX =  getResources().getDimension(R.dimen.battery_container_end_x) - mBatteryContainer.getWidth() * getBatteryContainerExtraTranslate()  - startPointOne[0];
        mBatteryContainerTranslateY = getResources().getDimension(R.dimen.battery_container_end_y) - mBatteryContainer.getHeight() * getBatteryContainerExtraTranslate() - startPointOne[1];
        mBatteryDesTranslateX = getResources().getDimension(R.dimen.battery_des_end_x) - mBatteryDes.getWidth() * getBatteryDesExtraTranslate() - startPointTwo[0];
        mBatteryDesTranslateY = getResources().getDimension(R.dimen.battery_des_end_y) - mBatteryDes.getHeight() * getBatteryDesExtraTranslate() - startPointTwo[1];
        mBatteryStatusTranslateY = getResources().getDimension(R.dimen.battery_status_end_y)  - startPointThree[1];
    }

    private float getBatteryContainerExtraTranslate(){
        return  (1 - mBatteryContainerScaleTo) /2;
    }

    private float getBatteryDesExtraTranslate(){
        return  (1 - mBatteryDesScaleTo) /2;
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            calculateTranslate();
            startTranslateAnim();
        }
    };
    private void startTranslateAnim(){

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                mBatteryContainer.setTranslationX(animatedFraction * mBatteryContainerTranslateX);
                mBatteryContainer.setTranslationY(animatedFraction * mBatteryContainerTranslateY);
                mBatteryContainer.setScaleX(1 - animatedFraction * (1 - mBatteryContainerScaleTo));
                mBatteryContainer.setScaleY(1 - animatedFraction * (1 - mBatteryContainerScaleTo));


                mBatteryDes.setTranslationX(animatedFraction * mBatteryDesTranslateX);
                mBatteryDes.setTranslationY(animatedFraction * mBatteryDesTranslateY);
                mBatteryDes.setScaleX(1 - animatedFraction * (1 - mBatteryDesScaleTo));
                mBatteryDes.setScaleY(1 - animatedFraction * (1 - mBatteryDesScaleTo));

                mBatteryStatus.setTranslationY(animatedFraction * mBatteryStatusTranslateY);

                mAdLayout.setTranslationY( (1f - animatedFraction) * mAdLayout.getHeight() );

                mTvBatteryProgress.setAlpha(1 - animatedFraction);
                mBatteryPercent.setAlpha(animatedFraction);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                mAdLayout.setVisibility(View.VISIBLE);
                mBatteryPercent.setVisibility(View.VISIBLE);
            }
        });
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    private void registerReceiver() {
        mBatteryReceiver = new BatteryDataReceiver(this, this);
    }

    @Override
    public void receiveBatteryData(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        mPercent = LockerUtils.getBatteryPercent(level, scale);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        //mBatteryProgress.setText(mPercent + "%");
        mTvBatteryProgress.setText(getResources().getString(R.string.battery_progress) + " " + mPercent + "%");
        mBatteryPercent.setText(mPercent + "%");
        mBpBatteryProgress.setProgress(mPercent);

        setCurrentStatusByPercent(mPercent);

        if (mIsCharging && mPercent != 100) {
            mChargeValue.setVisibility(View.VISIBLE);
            mShowUsageTime = false;
            mShowTrickleTime =false;
            mTimeLeftDes.setText(R.string.charging_time);
            mTimeLeftHourValue.setText(LockerUtils.getCharingHourValueStr(getApplicationContext(), intent, mPercent));
            mTimeLeftMinutesValue.setText(LockerUtils.getCharingMinutesValueStr(getApplicationContext(), intent, mPercent));
        }else if (mIsCharging && mPercent == 100 && !hasFullyCharged()){
            mChargeValue.setVisibility(View.VISIBLE);
            mShowTrickleTime = true;
            mTimeLeftDes.setText(R.string.trickle_charging_time);
            mTimeLeftHourValue.setText(0 + "" + 0);
            mTimeLeftMinutesValue.setText(getTrickleRemainingTime());
        }else if (mIsCharging && mPercent == 100 && hasFullyCharged()){
            mShowTrickleTime = false;
            mTimeLeftDes.setText(R.string.charged_completed);
            mChargeValue.setVisibility(View.INVISIBLE);
        } else{
            mChargeValue.setVisibility(View.VISIBLE);
            mShowUsageTime = true;
            mShowTrickleTime =false;
            mTimeLeftDes.setText(R.string.usage_time);
            String hour = Utils.getUsageHourValue(SettingsHelper.getUsageTime(getApplicationContext()));
            String mins = Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(getApplicationContext()));
            mTimeLeftHourValue.setText(hour);
            mTimeLeftMinutesValue.setText(mins);
        }
    }

    private String getTrickleRemainingTime() {
        long remainTime = FULLY_CHARGED_TIME_THRESHOLD - (System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext()));
        int time = (int) Math.ceil(remainTime * 1.0f / (1000 * 60));
        if (time < 10) {
            return 0 + "" + time ;
        } else return 10 + "";
    }


    private void setCurrentStatusByPercent(int percent) {
        Log.d(TAG, "setCurrentStatusByPercent: percent" + percent);
        if (mIsCharging) {
            if (percent <= 80 &&  mCurrentStatus != STATUS_SPEED) {
                mCurrentStatus = STATUS_SPEED;
                setSpeedStatus();
            } else if (percent == 100) {
                mCurrentStatus = STATUS_TRICKLE;
                setTrickleStatus();
            }else if (percent < 100 && percent > 80 && mCurrentStatus != STATUS_CONTINUOUS){
                mCurrentStatus = STATUS_CONTINUOUS;
                setContinuousStatus();
            }
        } else {
            mCurrentStatus = STATUS_NONE;
            mSpeed.setImageResource(R.drawable.ic_speed_transparent);
            mContinuous.setImageResource(R.drawable.ic_continuous_transparent);
            mTrickle.setImageResource(R.drawable.ic_trickle_transparent);
            mSpeedContainer.setImageResource(R.drawable.ic_circle_gray);
            mContinuousContainer.setImageResource(R.drawable.ic_circle_gray);
            mTrickleContainer.setImageResource(R.drawable.ic_circle_gray);
            mSpeedProgress.setVisibility(View.GONE);
            mContinuousProgress.setVisibility(View.GONE);
            mTrickleProgress.setVisibility(View.GONE);

            mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));
            mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));

            mTvSpeed.setTextColor(getResources().getColor(R.color.text_color_light_gray));
            mTvContinuous.setTextColor(getResources().getColor(R.color.text_color_light_gray));
            mTvTrickle.setTextColor(getResources().getColor(R.color.text_color_light_gray));
        }
    }

    private void setContinuousStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed);
        mContinuous.setImageResource(R.drawable.ic_continuous);
        mTrickle.setImageResource(R.drawable.ic_trickle_transparent);
        mSpeedContainer.setImageResource(R.drawable.ic_circle_white);
        mContinuousContainer.setImageResource(R.drawable.ic_circle_gray);
        mTrickleContainer.setImageResource(R.drawable.ic_circle_gray);
        mSpeedProgress.setVisibility(View.GONE);
        mContinuousProgress.setVisibility(View.VISIBLE);
        mTrickleProgress.setVisibility(View.GONE);

        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_white));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));

        mTvSpeed.setTextColor(getResources().getColor(R.color.text_color_white));
        mTvContinuous.setTextColor(getResources().getColor(R.color.text_color_white));
        mTvTrickle.setTextColor(getResources().getColor(R.color.text_color_light_gray));


        startRotateAnim(mContinuousProgress);
    }

    private void setTrickleStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed);
        mContinuous.setImageResource(R.drawable.ic_continuous);
        mTrickle.setImageResource(R.drawable.ic_trickle);
        mSpeedContainer.setImageResource(R.drawable.ic_circle_white);
        mContinuousContainer.setImageResource(R.drawable.ic_circle_white);
        if (hasFullyCharged()){
            mTrickleContainer.setImageResource(R.drawable.ic_circle_white);
            mTrickleProgress.setVisibility(View.GONE);
        } else {
            mTrickleContainer.setImageResource(R.drawable.ic_circle_gray);
            mTrickleProgress.setVisibility(View.VISIBLE);
        }
        mSpeedProgress.setVisibility(View.GONE);
        mContinuousProgress.setVisibility(View.GONE);


        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_white));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_white));

        mTvSpeed.setTextColor(getResources().getColor(R.color.text_color_white));
        mTvContinuous.setTextColor(getResources().getColor(R.color.text_color_white));
        mTvTrickle.setTextColor(getResources().getColor(R.color.text_color_white));

        startRotateAnim(mTrickleProgress);
    }

    private boolean hasFullyCharged() {
        if (SettingsHelper.getFullyChargedTime(getApplicationContext()) == 0) return false;
        if (System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext())
                > FULLY_CHARGED_TIME_THRESHOLD) return true;
        return false;
    }


    private void setSpeedStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed);
        mContinuous.setImageResource(R.drawable.ic_continuous_transparent);
        mTrickle.setImageResource(R.drawable.ic_trickle_transparent);
        mSpeedContainer.setImageResource(R.drawable.ic_circle_gray);
        mContinuousContainer.setImageResource(R.drawable.ic_circle_gray);
        mTrickleContainer.setImageResource(R.drawable.ic_circle_gray);
        mSpeedProgress.setVisibility(View.VISIBLE);
        mContinuousProgress.setVisibility(View.GONE);
        mTrickleProgress.setVisibility(View.GONE);

        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));

        mTvSpeed.setTextColor(getResources().getColor(R.color.text_color_white));
        mTvContinuous.setTextColor(getResources().getColor(R.color.text_color_light_gray));
        mTvTrickle.setTextColor(getResources().getColor(R.color.text_color_light_gray));

        startRotateAnim(mSpeedProgress);
    }

    private void startRotateAnim(final View speedProgress) {
        mRotateAnimator = ValueAnimator.ofFloat(0f, 360f);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                speedProgress.setRotation(animatedValue);
            }
        });
        mRotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setDuration(2000);
        mRotateAnimator.setStartDelay(500);
        mRotateAnimator.start();
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REFRESH_BATTERY:
                    if (!CleanUtil.shouldCleanMemory(getApplicationContext())) {
                        updateUsageTime();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 500);
                    break;
            }
            return false;
        }


    });

    private void updateUsageTime() {
        if (mShowUsageTime && mPercent != 0) {
            String hour = Utils.getUsageHourValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            String mins = Utils.getUsageMinutesValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            mTimeLeftDes.setText(R.string.usage_time);
            mTimeLeftHourValue.setText(hour);
            mTimeLeftMinutesValue.setText(mins);
        }else if (mShowTrickleTime && mPercent != 0){
            mShowTrickleTime = true;
            mTimeLeftDes.setText(R.string.trickle_charging_time);
            mTimeLeftHourValue.setText(0 + "" + 0);
            mTimeLeftMinutesValue.setText(getTrickleRemainingTime() + "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBatteryReceiver.unregisterReceiver();
        mHandler.removeMessages(REFRESH_BATTERY);
    }
}
