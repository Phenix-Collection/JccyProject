package com.abclauncher.powerboost.clean;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.abclauncher.powerboost.BaseActivity;
import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.bean.FacebookNativeAdBean;
import com.abclauncher.powerboost.clean.Adapter.AppRecyclerViewAdapter;
import com.abclauncher.powerboost.clean.bean.AppProcessInfo;
import com.abclauncher.powerboost.clean.utils.AppUtils;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.clean.utils.ScaleInAnimator;
import com.abclauncher.powerboost.clean.view.BoostAnimView;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.notification.cleantips.CleanTipsNotification;
import com.abclauncher.powerboost.receiver.BatteryDataReceiver;
import com.abclauncher.powerboost.util.AdUtil;
import com.abclauncher.powerboost.util.FacebookAdCallbackDtail;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.Utils;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;
import com.abclauncher.powerboost.view.CustomFrameLayout;
import com.abclauncher.powerboost.view.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.NativeAd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by shenjinliang on 16/12/26.
 */

public class MemoryCleanActivity extends BaseActivity {

    private static final String TAG = "MemoryCleanActivity";
    private AppRecyclerViewAdapter mAdapter;
    private static final String BATTERY_REFRESH_ACTION = "com.abclauncher.powerboost.battery.refresh";

    @InjectView(R.id.cleaned_app_recycler)
    RecyclerView mRecyclerView;

    @InjectView(R.id.boost_view)
    BoostAnimView mBoostAnimView;

    private Handler mHandler = new Handler();

    private long mMemorySize;

    @InjectView(R.id.usage_time_hour_value)
    TextView mUsageTimeHourValue;
    @InjectView(R.id.usage_time_minutes_value)
    TextView mUsageTimeMinutesValue;
    @InjectView(R.id.tv_num_app_clean)
    TextView mCleanNumTv;
    @InjectView(R.id.tv_app_clean_string)
    TextView mCleanStringTv;
    @InjectView(R.id.view_anim_mask)
    View mMaskView;
    @InjectView(R.id.memory_clean_result_layout)
    View mResultLayout;

    @InjectView(R.id.ic_clean_des)
    LinearLayout mCleanResultDes;
    @InjectView(R.id.ic_clean_done)
    ImageView mCleanResultDone;
    @InjectView(R.id.ic_clean_done_two)
    ImageView mCleanResultDoneTwo;
    @InjectView(R.id.extended_mins_value)
    TextView mExtendedMinsTv;


    @InjectView(R.id.extended_container)
    LinearLayout mExtendedContainer;
    @InjectView(R.id.time_left_container)
    LinearLayout mTimeLeftContainer;

    @InjectView(R.id.result_usage_time_hour_value)
    TextView mResultUsageHour;
    @InjectView(R.id.result_usage_time_minutes_value)
    TextView mResultUsageMins;

    @InjectView(R.id.tv_result_des)
    TextView mResultDes;


    //ad view
    /*@InjectView(R.id.native_ad_social_context)
    TextView mAdSocialText;*/
    @InjectView(R.id.native_ad_image)
    ImageView mAdCoverIv;
    @InjectView(R.id.native_ad_icon)
    ImageView mAdIconIv;
    @InjectView(R.id.native_ad_image_two)
    ImageView mAdIconIvTwo;
    @InjectView(R.id.native_ad_title)
    TextView mAdTitleIv;
    @InjectView(R.id.native_ad_body)
    TextView mAdBodyTv;
    @InjectView(R.id.native_ad_call_to_action)
    TextView mAdInstallTv;

    @InjectView(R.id.radar_one)
    View mRadarScan;
    @InjectView(R.id.radar_two)
    View mRadarPoint;
    @InjectView(R.id.radar_container)
    View mRadarContainer;
    @InjectView(R.id.clean_content)
    View mCleanContent;
    @InjectView(R.id.root_view)
    CustomFrameLayout mRootView;
    @InjectView(R.id.ad_action_btn)
    MaterialRippleLayout mAdActionBtn;

    private int[] startPointOne = new int[2];
    private int[] startPointTwo = new int[2];
    private float mCleanDoneScaleTo = 0.65f;
    private float mCleanDesScaleTo = 0.9f;
    private float mCleanDoneTranslateX, mCleanDoneTranslateY;
    private float mCleanDesTranslateX, mCleanDesTranslateY;
    private ArrayList<Integer> extendedTimeList = new ArrayList<>();
    private ArrayList<Float> tempExtendedTimeList = new ArrayList<>();


    @InjectView(R.id.ad_layout)
    LinearLayout mAdLayout;
    private Runnable scanAnim = new Runnable() {
        @Override
        public void run() {
            runScanAnim();
            runPointAnim();
        }
    };

    private ValueAnimator mRotateAnimator;
    private float mExtendMinsValue;
    private boolean shouldCleanMemory;
    private boolean mAdIsLoaded = false;
    private boolean mHasDestroy;
    private boolean mBgShouldAnim;
    private int mHourValue;
    private BatteryReceiver mBatteryReceiver;
    private int mPercent;
    private int originMinutes;
    private String coverImgUrl;

    private void runPointAnim() {
        Animator animator = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.radar_point_anim);
        animator.setTarget(mRadarPoint);
        animator.start();
    }
    private void runScanAnim() {
        mRotateAnimator = ValueAnimator.ofFloat(0f, 360f);
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                mRadarScan.setRotation(animatedValue);
            }
        });
        mRotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setDuration(800);
        mRotateAnimator.setStartDelay(500);
        mRotateAnimator.start();
    }


    @OnClick(R.id.back)
    public void onBackIconClicked(){
        onBackPressed();
    }

    private NativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean);
        ButterKnife.inject(this);


        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        mAdapter = new AppRecyclerViewAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new ScaleInAnimator());

        mRadarPoint.setRotation(new Random().nextInt(360));

        mHandler.postDelayed(scanAnim, 0);
        mBoostAnimView.setVisibility(View.GONE);

        mHourValue = Integer.valueOf(Utils.getUsageHourValue(SettingsHelper.getUsageTime(getApplicationContext())));
        originMinutes = Integer.valueOf(Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(getApplicationContext())));

        Log.d(TAG, "onCreate: mHourValue" + mHourValue);

        mUsageTimeHourValue.setText(Utils.getUsageHourValue(SettingsHelper.getUsageTime(getApplicationContext())));
        mUsageTimeMinutesValue.setText(Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(getApplicationContext())));

        initAd();

        registerReceiver();

        mBgShouldAnim = getIntent().getBooleanExtra("bgShouldAnim", false);
        if (mBgShouldAnim) {
            mRootView.setStartColorAndEndColor(getResources().getColor(R.color.red_start_color),
                    getResources().getColor(R.color.red_end_color));
        } else {
            mRootView.setStartColorAndEndColor(getResources().getColor(R.color.blue_start_color),
                    getResources().getColor(R.color.blue_end_color));
        }
        startScanAppAsync();
        //int i = 10 / 0;

        CleanTipsNotification.cancelNotification(getApplicationContext());
    }

    private void registerReceiver() {
        mBatteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryReceiver, intentFilter);
    }

    public class BatteryReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            mPercent = LockerUtils.getBatteryPercent(level, scale);
        }
    }

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    private void initAd(){
        AdUtil.loadNativeAd(this, AdUtil.AD_PLACEMENT_CLEAN_RESULT, new FacebookAdCallbackDtail() {
            @Override
            public void onNativeAdLoadError() {
            }

            @Override
            public void onNativeAdClick(Ad ad) {
                ad.destroy();
            }

            @Override
            public void onNativeAdLoaded(FacebookNativeAdBean facebookNativeAdBean) {
                mAdIsLoaded = true;
                String textForAdTitle = facebookNativeAdBean.title;
                coverImgUrl = facebookNativeAdBean.coverImgUrl;
                String iconForAdUrl = facebookNativeAdBean.iconForAdUrl;
                String textForAdBody = facebookNativeAdBean.textForAdBody;
                String adAction = facebookNativeAdBean.actionBtnText;
                nativeAd = facebookNativeAdBean.nativeAd;

                mAdTitleIv.setText(textForAdTitle);
                if (TextUtils.isEmpty(adAction)) {
                    mAdInstallTv.setVisibility(View.GONE);
                } else {
                    mAdInstallTv.setText(adAction);
                }
                mAdBodyTv.setText(textForAdBody);

                Glide.with(getApplicationContext())
                        .load(iconForAdUrl)
                        .into(mAdIconIv);
                facebookNativeAdBean.nativeAd.registerViewForInteraction(mAdLayout);
            }
        });
    }

    private Runnable getStopRadarRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                if (shouldCleanMemory && apps.size() > 0) {
                    mCleanNumTv.setText(0 + " ");
                    mCleanStringTv.setText(R.string.memory_clean_app_text);
                    mRotateAnimator.cancel();
                    mRadarContainer.setVisibility(View.GONE);
                    mBoostAnimView.setVisibility(View.VISIBLE);
                    mBoostAnimView.start();
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAdResultLayout();
                        }
                    }, 500);
                }
            }
        };
    }

    private  List<AppProcessInfo> apps = new ArrayList<>();

    private int mRepeateCount;
    private void startScanAppAsync(){
        mExtendMinsValue = 0;
        shouldCleanMemory = CleanUtil.shouldCleanMemory(getApplicationContext());
        if (shouldCleanMemory){
            mResultDes.setText(R.string.clean_result_des);
        }else {
            mExtendedContainer.setVisibility(View.GONE);
            mTimeLeftContainer.setVisibility(View.VISIBLE);
            mResultDes.setText(R.string.time_left);
            mResultUsageHour.setText(Utils.getUsageHourValue(SettingsHelper.getUsageTime(getApplicationContext())));
            mResultUsageMins.setText(Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(getApplicationContext())));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppUtils.getRunningProcessInfo(getApplicationContext(), new AppUtils.ScanApps(){
                    @Override
                    public void scanApp(AppProcessInfo info) {
                        if (apps.size() < 30) {
                            Log.d(TAG, "scanApp: percent-->" + mPercent);
                            if (shouldCleanMemory){
                                if (info.isSystem){
                                    mExtendMinsValue = mExtendMinsValue + 3 * mPercent / 100.0f;
                                }else {
                                    mExtendMinsValue = mExtendMinsValue + 5 * mPercent / 100.0f;
                                }

                                apps.add(info);
                                notifyScanProcess(info);
                            }
                        }
                    }
                });
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for(AppProcessInfo info : apps) {
                            killBackgroundProcesses(info.processName);
                        }
                        initExtendedList();
                    }
                }, 900);
                mHandler.postDelayed(getStopRadarRunnable(),1000);


                int count = getCount();
                for (int i = 0; i < count; i++) {
                    notifyCleanProcess(i , 1000 * (i + 1));
                }

                if (mBgShouldAnim) {
                    mHandler.postDelayed(colorAnim,1000);
                }

            }
        }).start();
    }

    private void initExtendedList() {
            for (int i = 0;  i < getCount(); i++ ){
                float extendedTime = 0;
                if (i == getCount() - 1) {
                    for (int j = i * 6 ; j < apps.size(); j++) {
                        if (apps.get(j).isSystem) {
                            extendedTime =  extendedTime + 3 * mPercent / 100.0f;
                        } else {
                            extendedTime = extendedTime + 5 * mPercent / 100.0f;
                        }
                    }
                }else {
                    for (int j = i * 6 ; j < (i + 1) * 6; j++) {
                        if (apps.get(j).isSystem) {
                            extendedTime =  extendedTime + 3 * mPercent / 100.0f;
                        } else {
                            extendedTime = extendedTime + 5 * mPercent / 100.0f;
                        }
                    }
                }
                tempExtendedTimeList.add(extendedTime);
                float totalTime = 0;
                for (Float f:
                     tempExtendedTimeList) {
                    totalTime += f;
                }
               extendedTimeList.add(Math.round(totalTime));
            }

        for (Integer integer : extendedTimeList) {
            Log.d(TAG, "initExtendedList: " + integer);
        }
    }

    private Runnable colorAnim = new Runnable() {
        @Override
        public void run() {
            int blueStartColor = getResources().getColor(R.color.blue_start_color);
            int redStartColor = getResources().getColor(R.color.red_start_color);
            int blueEndColor = getResources().getColor(R.color.blue_end_color);
            int redEndColor = getResources().getColor(R.color.red_end_color);
            PropertyValuesHolder startColor = PropertyValuesHolder.ofObject( "startColor", new ArgbEvaluator(), redStartColor, blueStartColor);
            PropertyValuesHolder endColor = PropertyValuesHolder.ofObject("endColor", new ArgbEvaluator(), redEndColor, blueEndColor);

            ValueAnimator valueAnimator1 = ValueAnimator.ofPropertyValuesHolder(startColor, endColor);
            valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int startColor1 = (int) animation.getAnimatedValue("startColor");
                    int endColor1 = (int) animation.getAnimatedValue("endColor");
                    mRootView.setStartColorAndEndColor(startColor1, endColor1);
                }
            });

            valueAnimator1.setDuration(getCount() * 800);
            //valueAnimator1.setStartDelay(2000);
            valueAnimator1.setInterpolator(new LinearInterpolator());
            valueAnimator1.start();
        }
    };
    private int getCount() {
        if (apps.size() % 6 == 0) {
            return apps.size()/6;
        } else {
            return apps.size()/6 + 1;
        }
    }

    private void showAdResultLayout(){
        computeParameters();

        mExtendedMinsTv.setText(Math.round(mExtendMinsValue) + "");

        //ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mMaskView, "alpha", 0, 0.2f);
        //ObjectAnimator tranAnim = ObjectAnimator.ofFloat(mResultLayout, "translationY", transY, 0);

        Animator cleanContentAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.clean_content_dismiss_anim);
        cleanContentAnim.setTarget(mCleanContent);
        cleanContentAnim.setDuration(100);


        //对勾的 动画
        PropertyValuesHolder scale = PropertyValuesHolder.ofFloat("scale", 0.8f, 1.2f, 1f);
        ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(scale);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue("scale");
                mCleanResultDone.setScaleX(animatedValue);
                mCleanResultDone.setScaleY(animatedValue);
            }
        });
        animator.setDuration(800);

        //对勾背后 圆圈的动画
        Animator cleanDoneCircleAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.clean_done_circle_anim);
        cleanDoneCircleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mCleanResultDoneTwo.setVisibility(View.VISIBLE);
            }
        });
        cleanDoneCircleAnim.setTarget(mCleanResultDoneTwo);
        cleanDoneCircleAnim.setDuration(800);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(cleanContentAnim).with(animator);

        AnimatorSet totalSetAnim = new AnimatorSet();
        totalSetAnim.playSequentially(animatorSet, cleanDoneCircleAnim);
        if (!mAdIsLoaded){
            totalSetAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                   /* mMaskView.setVisibility(View.VISIBLE);
                    mMaskView.setAlpha(0);*/

                    //mCleanContent.setVisibility(View.GONE);
                    mResultLayout.setVisibility(View.VISIBLE);
                    mBoostAnimView.setDisappear();
                    //在这 判断是清理时间节点
                    if (!mHasDestroy && shouldCleanMemory){
                        Log.d(TAG, "setLastCleanTime: ");
                        //set clean time
                        SettingsHelper.setLastCleanTime(getApplicationContext(), System.currentTimeMillis());
                        long usageTime = SettingsHelper.getUsageTime(getApplicationContext()) + Math.round(mExtendMinsValue) * 1000 * 60;
                        SettingsHelper.setUsageTime(getApplicationContext(), usageTime);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            totalSetAnim.start();
        } else {
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(totalSetAnim, getScaleAndTranslationAnim());
            set.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {

                    /*mMaskView.setVisibility(View.VISIBLE);
                    mMaskView.setAlpha(0);
                    mResultLayout.setVisibility(View.VISIBLE);
                    mBoostAnimView.setDisappear();
                    mResultLayout.setTranslationY(transY);*/
                   // mCleanContent.setVisibility(View.GONE);
                    mResultLayout.setVisibility(View.VISIBLE);
                    mBoostAnimView.setDisappear();
                    //在这 判断是清理时间节点
                    if (!mHasDestroy && shouldCleanMemory){
                        Log.d(TAG, "setLastCleanTime: ");
                        //set clean time
                        SettingsHelper.setLastCleanTime(getApplicationContext(), System.currentTimeMillis());
                        long usageTime = SettingsHelper.getUsageTime(getApplicationContext()) + Math.round(mExtendMinsValue) * 1000 * 60;
                        SettingsHelper.setUsageTime(getApplicationContext(), usageTime);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                    }
                }
            });

            set.start();
        }
    }

    private AnimatorSet getScaleAndTranslationAnim() {
        Animator cleanContentAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.clean_content_dismiss_anim);
        cleanContentAnim.setTarget(mResultLayout);
        cleanContentAnim.setDuration(100);


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
             /*   mCleanResultDone.setTranslationX(mCleanDoneTranslateX * animatedFraction);
                mCleanResultDone.setTranslationY(mCleanDoneTranslateY * animatedFraction);
                mCleanResultDone.setScaleX(1 - animatedFraction * (1 - mCleanDoneScaleTo));
                mCleanResultDone.setScaleY(1 - animatedFraction * (1 - mCleanDoneScaleTo));

                mCleanResultDes.setTranslationX(mCleanDesTranslateX * animatedFraction);
                mCleanResultDes.setTranslationY(mCleanDesTranslateY * animatedFraction);
                mCleanResultDes.setScaleX(1 - animatedFraction * (1 - mCleanDesScaleTo));
                mCleanResultDes.setScaleY(1 - animatedFraction * (1 - mCleanDesScaleTo));*/

                mAdLayout.setTranslationY((1 - animatedFraction) * mAdLayout.getHeight() * 1.2f);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mAdLayout.setVisibility(View.VISIBLE);
                mRootView.setShowBubble(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAdActionBtn.startAnim();
                if (!TextUtils.isEmpty(coverImgUrl)){
                    Glide.with(getApplicationContext())
                            .load(coverImgUrl)
                            .into(mAdIconIvTwo);
                    Glide.with(getApplicationContext())
                            .load(coverImgUrl)
                            .into(mAdCoverIv);

                }

            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(cleanContentAnim, valueAnimator);

        return animatorSet;
    }

    @Override
    protected void onDestroy() {
        mHasDestroy = true;
        super.onDestroy();
        unregisterReceiver(mBatteryReceiver);
    }

    private void computeParameters() {
        mCleanResultDone.getLocationOnScreen(startPointOne);
        mCleanResultDes.getLocationOnScreen(startPointTwo);
        mCleanDoneTranslateX =  getResources().getDimension(R.dimen.clean_done_image_end_x) - mCleanResultDone.getWidth() * getCleanDoneExtraTranslate()  - startPointOne[0];
        mCleanDoneTranslateY = getResources().getDimension(R.dimen.clean_done_image_end_y) - mCleanResultDone.getHeight() * getCleanDoneExtraTranslate() - startPointOne[1];
        mCleanDesTranslateX = getResources().getDimension(R.dimen.clean_done_tv_end_x) - mCleanResultDes.getWidth() * getCleanDesExtraTranslate() - startPointTwo[0];
        mCleanDesTranslateY = getResources().getDimension(R.dimen.clean_done_tv_end_y) - mCleanResultDes.getHeight() * getCleanDesExtraTranslate() - startPointTwo[1];
    }

    private float getCleanDoneExtraTranslate() {
        return  (1 - mCleanDoneScaleTo) /2;
    }

    private float getCleanDesExtraTranslate() {
        return  (1 - mCleanDesScaleTo) /2;
    }


    private void notifyCleanProcess(final int count, long delay) {
        Log.d(TAG, "notifyCleanProcess: delay" + delay);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRepeateCount++;
                Log.d(TAG, "run: " + getCount());
                Log.d(TAG, "run: mRepeateCount" + mRepeateCount);
                if (mRepeateCount < getCount()) {
                    mAdapter.removeItem(6);
                    mCleanNumTv.setText(mRepeateCount * 6 + " ");
                }else {
                    if (apps.size() % 6 == 0) {
                        mAdapter.removeItem(6);
                        mCleanNumTv.setText(apps.size() + " ");
                    }else {
                        mAdapter.removeItem(apps.size()%6);
                        mCleanNumTv.setText(apps.size() + " ");
                    }

                }

                //更新 分钟时间
                originMinutes = Integer.valueOf(Utils.getUsageMinutesValue(SettingsHelper.getUsageTime(getApplicationContext())));
                Log.d(TAG, "run:originMinutes---> " + originMinutes);
                Log.d(TAG, "run:mMinutesValue---> " + extendedTimeList.get(mRepeateCount - 1));
                int minutesValue = originMinutes + extendedTimeList.get(mRepeateCount - 1);
                mUsageTimeHourValue.setText(getHourValueStr(minutesValue));
                minutesValue = minutesValue % 60;
                mUsageTimeMinutesValue.setText(getMinsValueStr(minutesValue));
                if(mRepeateCount == getCount()){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAdResultLayout();
                        }
                    }, 500);

                }
            }
        }, delay);
    }

    private String getMinsValueStr(int minutesValue) {
        if (minutesValue < 10) {
            return 0 + "" + minutesValue;
        } else {
            return minutesValue + "";
        }
    }

    private String getHourValueStr(int minutesValue) {
        int hourValue = mHourValue + minutesValue / 60;
        if (hourValue < 10) {
            return 0 + "" + hourValue;
        } else {
            return hourValue + "";
        }

    }

    private void notifyScanProcess(final AppProcessInfo info) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCleanNumTv.setText(apps.size() + "");
                mAdapter.addItem(info);
            }
        },200);
    }

    public void killBackgroundProcesses(String processName) {
        String packageName = null;
        if (processName.indexOf(":") == -1) {
            packageName = processName;
        } else {
            packageName = processName.split(":")[0];
        }

        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(packageName);
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod(
                            "forceStopPackage",
                            String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);

        } catch (Exception e) {

        }
    }

    public static void startMemoryCleanActivity(Context context){
        try {
            Intent intent = new Intent(context, MemoryCleanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(new Intent(intent));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
