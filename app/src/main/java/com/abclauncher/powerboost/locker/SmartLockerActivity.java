package com.abclauncher.powerboost.locker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.bean.FacebookNativeAdBean;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.locker.base.BaseLockerActivity;
import com.abclauncher.powerboost.locker.base.SwipeBackLayout;
import com.abclauncher.powerboost.locker.utils.LockerReceiver;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.util.AdHelper;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.view.BatteryProgress;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import butterknife.InjectView;


public class SmartLockerActivity extends BaseLockerActivity implements LockerReceiver.LockerReceiverCallback {
    private static final String TAG = "SmartLockerActivity";
    private static final int REFRESH_BATTERY = 1000;
    @InjectView(R.id.simple_locker_time)
    TextView mLockerTimeTv;
    @InjectView(R.id.simple_locker_date)
    TextView mLockerDateTv;
    @InjectView(R.id.time_left)
    TextView mTimeLeftDes;

    @InjectView(R.id.ic_speed)
    ImageView mSpeed;
    @InjectView(R.id.ic_continuous)
    ImageView mContinuous;
    @InjectView(R.id.ic_trickle)
    ImageView mTrickle;


    @InjectView(R.id.link_one)
    View mLinkOne;
    @InjectView(R.id.link_two)
    View mLinkTwo;

    @InjectView(R.id.bp_battery_progress)
    BatteryProgress mBpBatteryProgress;
    @InjectView(R.id.tv_battery_progress)
    TextView mBatteryProgress;

    @InjectView(R.id.unlock_tips_text)
    ShimmerTextView mUnLockTv;
    @InjectView(R.id.slide_layout)
    FrameLayout mSlideMainFL;

    /** 广告 **/
    @InjectView(R.id.smart_locker_ad_cl)
    LinearLayout mAdCoordinatorLayout;
    @InjectView(R.id.battery_ad_main_ll)
    LinearLayout mAdMainLl;
    @InjectView(R.id.battery_ad_iv)
    ImageView mAdCoverIv;
    @InjectView(R.id.simple_locker_ad_icon)
    ImageView mAdIconIv;
    @InjectView(R.id.simple_locker_ad_title)
    TextView mAdTitleIv;
    @InjectView(R.id.simple_locker_ad_action_tv)
    TextView mAdActionTv;
    @InjectView(R.id.battery_ad_summary)
    TextView mAdBodyIv;



    @InjectView(R.id.locker_charge_time_container)
    View mLockerChargeTimeContainer;

    @InjectView(R.id.usage_time_hour_value)
    TextView mTimeLeftHourValue;
    @InjectView(R.id.usage_time_minutes_value)
    TextView mTimeLeftMinutesValue;
    @InjectView(R.id.charge_value)
    View mChargeValue;

    String mSlideStr;
    private NativeAd nativeAd;
    private boolean isAdLoaded = false;
    private Shimmer mShimmer;
   /** ---------------广告end-----------------**/


    private LockerReceiver mLockerReceiver;
    private int mCurrentStatus = STATUS_NONE;
    private ValueAnimator mRotateAnimator;
    private static  final int STATUS_NONE= -1;
    private static  final int STATUS_SPEED = 0;
    private static  final int STATUS_CONTINUOUS = 1;
    private static  final int STATUS_TRICKLE = 2;
    private static final long FULLY_CHARGED_TIME_THRESHOLD = 1000 * 60 * 10;
    private boolean mIsCharging;
    private boolean mShowTrickleTime;
    private int mPercent;

    public static void start(Context context) {
        Intent intent = new Intent(context, SmartLockerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_locker;
    }

    @Override
    public void initViews() {
        initWindow();
        getSwipeBackLayout().setEnablePullToBack(true);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        mSlideStr = getResources().getString(R.string.charging_screen_slide_to_enter);
        mLockerReceiver = new LockerReceiver(this, this);
        mUnLockTv.setText(mSlideStr );
        Typeface fontFace = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Regular.ttf");
        mUnLockTv.setTypeface(fontFace);
        mShimmer = new Shimmer();
        mShimmer.setDuration(2800);
        mShimmer.start(mUnLockTv);
        initAd();

        mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 0);
    }
    private void initAd(){
        mAdMainLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdCoverIv.performClick();
            }
        });
        AdHelper.showAd(this, AdHelper.AdIds.AD_SIMPLE_LOCKER, new AdHelper.FbAdCallback() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "onError: " + adError.getErrorMessage());
            }

            @Override
            public void onNativeAdLoaded(FacebookNativeAdBean facebookNativeAdBean) {
                try {
                    getSwipeBackLayout().setRangeView(mAdCoordinatorLayout);
                    mAdMainLl.setVisibility(View.VISIBLE);
                    isAdLoaded = true;
                    String textForAdTitle = facebookNativeAdBean.title;
                    String coverImgUrl = facebookNativeAdBean.coverImgUrl;
                    String iconForAdUrl = facebookNativeAdBean.iconForAdUrl;
                    String textForAdBody = facebookNativeAdBean.textForAdBody;
                    String adAction = facebookNativeAdBean.actionBtnText;
                    nativeAd = facebookNativeAdBean.nativeAd;
                    mAdTitleIv.setText(textForAdTitle);
                    if (TextUtils.isEmpty(adAction)) {
                        mAdActionTv.setVisibility(View.GONE);
                    } else {
                        mAdActionTv.setText(adAction);
                    }
                    mAdBodyIv.setText(textForAdBody);
                    Glide.with(getApplicationContext()).load(coverImgUrl).into(mAdCoverIv);
                    Glide.with(getApplicationContext()).load(iconForAdUrl).into(mAdIconIv);
                    facebookNativeAdBean.nativeAd.registerViewForInteraction(mAdCoverIv);
                }catch (Exception e){

                }
            }
            @Override
            public void onAdClicked(Ad ad) {
                isAdLoaded = false;
                ad.destroy();
            }
        });
    }
    private void initWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLockerReceiver.unregisterLockerReceiver(this);
        mShimmer.cancel();
        if(nativeAd != null){
            nativeAd.destroy();
        }

        mHandler.removeMessages(REFRESH_BATTERY);
    }

    @Override
    public void receiveCpuPercent(int cpuPercent) {
    }

    @Override
    public void receiveTemperature(int temp) {
    }

    @Override
    public void receiveTime(String time) {
        mLockerTimeTv.setText(time);
    }

    @Override
    public void receiveDate(String data) {
        mLockerDateTv.setText(data);
    }

    @Override
    public void receiveBatteryData(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        mPercent = LockerUtils.getBatteryPercent(level, scale);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        //mWaveView.setProgress(mPercent);
        mBpBatteryProgress.setProgress(mPercent);
        mBatteryProgress.setText(mPercent + "");

        if (mIsCharging) {
            mLockerChargeTimeContainer.setVisibility(View.VISIBLE);
            if (mIsCharging && mPercent != 100) {
                mChargeValue.setVisibility(View.VISIBLE);
                mShowTrickleTime =false;
                mTimeLeftDes.setText(R.string.charging_time_des);
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
                mChargeValue.setVisibility(View.GONE);
            }
        }else {
            mLockerChargeTimeContainer.setVisibility(View.INVISIBLE);
        }


        setCurrentStatusByPercent(mPercent);

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REFRESH_BATTERY:
                    if (!CleanUtil.shouldCleanMemory(getApplicationContext())) {
                        updateChargeLeftTime();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 500);
                    break;
            }
            return false;
        }


    });

    private String getTrickleRemainingTime() {
        long remainTime = FULLY_CHARGED_TIME_THRESHOLD - (System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext()));
        int time = (int) Math.ceil(remainTime * 1.0f / (1000 * 60));
        if (time < 10) {
            return 0 + "" + time ;
        } else return 10 + "";
    }

    private void updateChargeLeftTime() {
       if (mShowTrickleTime && mPercent != 0){
            mShowTrickleTime = true;
            mTimeLeftDes.setText(R.string.trickle_charging_time);
            mTimeLeftHourValue.setText(0 + "" + 0);
            mTimeLeftMinutesValue.setText(getTrickleRemainingTime() + "");
        }
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
            if (mRotateAnimator != null) {
                mRotateAnimator.cancel();
            }
            mCurrentStatus = STATUS_NONE;
            mSpeed.setImageResource(R.drawable.icon_speed_transparent);
            mSpeed.setAlpha(255);
            mContinuous.setImageResource(R.drawable.ic_continue_transparent);
            mContinuous.setAlpha(255);
            mTrickle.setImageResource(R.drawable.ic_full_transparent);
            mTrickle.setAlpha(255);


            mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));
            mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));

        }
    }

    private void setContinuousStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed_nomal);
        mSpeed.setAlpha(255);
        mContinuous.setImageResource(R.drawable.ic_continue_nomal);
        mContinuous.setAlpha(255);
        mTrickle.setImageResource(R.drawable.ic_full_transparent);
        mTrickle.setAlpha(255);

        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_white));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));


        startAlphaAnim(mContinuous);
    }

    private void setTrickleStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed_nomal);
        mSpeed.setAlpha(255);
        mContinuous.setImageResource(R.drawable.ic_continue_nomal);
        mContinuous.setAlpha(255);
        mTrickle.setImageResource(R.drawable.ic_full_nomal);
        mTrickle.setAlpha(255);
        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_white));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_white));
        if (hasFullyCharged()){
            if (mRotateAnimator != null) {
                mRotateAnimator.cancel();
            }
            mTrickle.setImageResource(R.drawable.ic_full_nomal);
            mTrickle.setAlpha(255);
            return;
        } else {
            startAlphaAnim(mTrickle);
        }

    }


    private boolean hasFullyCharged() {
        if (SettingsHelper.getFullyChargedTime(getApplicationContext()) == 0) return false;
        if (System.currentTimeMillis() - SettingsHelper.getFullyChargedTime(getApplicationContext())
                > FULLY_CHARGED_TIME_THRESHOLD) return true;
        return false;
    }


    private void setSpeedStatus() {
        mSpeed.setImageResource(R.drawable.ic_speed_nomal);
        mSpeed.setAlpha(255);
        mContinuous.setImageResource(R.drawable.ic_continue_transparent);
        mContinuous.setAlpha(255);
        mTrickle.setImageResource(R.drawable.ic_full_transparent);
        mTrickle.setAlpha(255);

        mLinkOne.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));
        mLinkTwo.setBackgroundColor(getResources().getColor(R.color.text_color_light_gray));

        startAlphaAnim(mSpeed);
    }



    private void startAlphaAnim(final View speedProgress) {
        if (mRotateAnimator != null) mRotateAnimator.cancel();
        mRotateAnimator = ValueAnimator.ofFloat(1f, 0.15f);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                speedProgress.setAlpha(animatedValue);
            }
        });
        mRotateAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setDuration(800);
        mRotateAnimator.setStartDelay(500);
        mRotateAnimator.start();
    }

    @Override
    public void receiveHomeClick() {
        finish();
    }
}
