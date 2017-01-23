package com.abclauncher.powerboost;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.abclauncher.powerboost.bean.AppInfo;
import com.abclauncher.powerboost.clean.MemoryCleanActivity;
import com.abclauncher.powerboost.clean.bean.AppProcessInfo;
import com.abclauncher.powerboost.clean.utils.AppUtils;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.locker.utils.LockerUtils;
import com.abclauncher.powerboost.mode.ModeActivity;
import com.abclauncher.powerboost.receiver.BatteryDataReceiver;
import com.abclauncher.powerboost.service.BatteryService;
import com.abclauncher.powerboost.share.ShareDialog;
import com.abclauncher.powerboost.util.AnalyticsHelper;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.StatsUtil;
import com.abclauncher.powerboost.util.Utils;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;
import com.abclauncher.powerboost.view.BatteryProgress;
import com.abclauncher.powerboost.view.CustomFrameLayout;
import com.abclauncher.powerboost.view.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements BatteryDataReceiver.BatteryCallback {

    private static final String ACTION_CLEAN = "clean";
    private static final String ACTION_CHARGE = "charge";
    private static final int REFRESH_BATTERY = 3;
    private static final int FIRST_PRESSED_BACK = 4;
    private static final int CHARGE_CODE = 2;

    @InjectView(R.id.menu_container)
    View mMenuContainer;
    private BatteryDataReceiver mBatteryReceiver;
    private boolean mIsCharging;

    @InjectView(R.id.bp_battery_progress)
    BatteryProgress mBpBatteryProgress;

    @InjectView(R.id.time_left_des)
    TextView mUsageTimeDes;
    @InjectView(R.id.left_time_hour_value)
    TextView mTimeLeftHourValue;
    @InjectView(R.id.left_time_mins_value)
    TextView mTimeLeftMinutesValue;

    @InjectView(R.id.optimize_text)
    TextView mOptimizeText;

    //icon battery
    @InjectView(R.id.battery_container)
    View mBatteryContainer;
    //icon find_problem
    @InjectView(R.id.find_problem_container)
    View mProblemContainer;

    @InjectView(R.id.usage_time_container)
    View mUsageTimeContainer;

    @InjectView(R.id.find_apps_consume_power_container)
    View mAppConsumePowerContainer;

    @InjectView(R.id.btn_optimize_container)
    View mOptimizeContainer;
    @InjectView(R.id.found_problem_des)
    TextView mFoundProblemDes;

    private int mBatteryPercent;
    private boolean mShowUsageTime;
    private final String TAG = "MainActivity";
    private int mPercent;
    private boolean firstPressBack = true;

    @OnClick(R.id.menu)
    public void onMenuOnClicked(){
        showMenu();
    }

    private void showMenu() {
        mMenuContainer.setVisibility(View.VISIBLE);
    }

    private void hideMenu() {
        mMenuContainer.setVisibility(View.GONE);
    }

    @InjectView(R.id.tv_battery_progress)
    TextView mBatteryProgress;

   /* @OnClick(R.id.white_list)
    public void openWhiteListSettingActivity(){
        hideMenu();
        Toast.makeText(this, "White List", Toast.LENGTH_SHORT).show();
    }*/

    @OnClick(R.id.settings)
    public void openSettingsActivity(){
        AnalyticsHelper.sendEvent(StatsUtil.MENU_CATOGORY, StatsUtil.MENU_CATOGORY_SETTINGS);
        hideMenu();
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.share)
    public void onShareItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MENU_CATOGORY, StatsUtil.MENU_CATOGORY_SHARE);
        hideMenu();
        ShareDialog dialog = new ShareDialog(this);
        dialog.show();
    }

    @OnClick(R.id.rate)
    public void onRateItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MENU_CATOGORY, StatsUtil.MENU_CATOGORY_RATE);
        hideMenu();
        gotoPowerBoostPage();
    }

    @OnClick(R.id.feedback)
    public void onFeedbackItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MENU_CATOGORY, StatsUtil.MENU_CATOGORY_FEEDBACK);
        hideMenu();
        Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.about_us)
    public void onAboutUsItemClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MENU_CATOGORY, StatsUtil.MENU_CATOGORY_ABOUT_US);
        hideMenu();
        Intent intent = new Intent(this, AboutUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.rank)
    public void onRankClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MAIN_PAGE, StatsUtil.MAIN_PAGE_RANK);
        Intent intent = new Intent(this, RankActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.mode)
    public void onModeClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MAIN_PAGE, StatsUtil.MAIN_PAGE_MODE);
        Intent intent = new Intent(this, ModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.charge)
    public void onChargeClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MAIN_PAGE, StatsUtil.MAIN_PAGE_CHARGE);
        Log.d(TAG, "onChargeClicked: ");
        Intent intent = new Intent(this, ChargeActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @InjectView(R.id.optimize_btn)
    MaterialRippleLayout mOptimizeBtn;

    @OnClick(R.id.optimize_btn)
    public void onOptimizeBtnClicked(){
        SettingsHelper.setCleanTimes(getApplicationContext(), SettingsHelper.getCleanTimes(getApplicationContext()) + 1);
        AnalyticsHelper.sendEvent(StatsUtil.MAIN_PAGE, StatsUtil.MAIN_PAGE_OPTIMIZE);
        Intent intent = new Intent(getApplicationContext(), MemoryCleanActivity.class);
        intent.putExtra("bgShouldAnim", mRootView.getStartColor() != getResources().getColor(R.color.blue_start_color));
        startActivityForResult(intent, CHARGE_CODE);
    }

    @InjectView(R.id.root_view)
    CustomFrameLayout mRootView;

    @OnClick(R.id.charge_screen_tips)
    public void OnChargeScreenTipsClicked(){
        AnalyticsHelper.sendEvent(StatsUtil.MAIN_PAGE, StatsUtil.MAIN_PAGE_LOCK_SCREEN_TIPS);
        SettingsHelper.setLockScreenOpened(getApplicationContext(), true);
        mChargeScreenTips.setVisibility(View.INVISIBLE);
    }


    @InjectView(R.id.charge_screen_tips)
    View mChargeScreenTips;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ACTION_CLEAN.equals(getIntent().getStringExtra("start"))){
            Log.d(TAG, "onCreate: ");
            Intent intent = new Intent(getApplicationContext(), MemoryCleanActivity.class);
            startActivity(intent);
        }else if (ACTION_CHARGE.equals(getIntent().getStringExtra("start"))){
            Log.d(TAG, "onCreate: ");
            Intent intent= new Intent(getApplicationContext(), ChargeActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        BatteryService.start(getApplicationContext());

        registerReceiver();

        if (CleanUtil.shouldCleanMemory(getApplicationContext())) {
            mOptimizeText.setText(R.string.scaning);
            new Thread(checkConsumePowerApps).start();
            mHandler.postDelayed(colorAnim, 200);
        } else {
            mOptimizeContainer.setScaleX(1.05f);
            mOptimizeContainer.setScaleY(1.05f);
        }

        mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 1000);

    }

    private List<AppProcessInfo> apps = new ArrayList();
    private Runnable checkConsumePowerApps = new Runnable() {
        @Override
        public void run() {
            AppUtils.getRunningProcessInfo(getApplicationContext(), new AppUtils.ScanApps(){
                @Override
                public void scanApp(AppProcessInfo info) {
                    if (apps.size() < 30) {
                        Log.d(TAG, "scanApp: percent-->" + mPercent);
                        apps.add(info);
                    }
                }
            });
            Log.d(TAG, "run: " + apps.size());
            mFoundProblemDes.setText(apps.size() + " " + getResources().getString(R.string.scan_memory));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHARGE_CODE && resultCode == RESULT_OK) {
            mRootView.setStartColorAndEndColor(getResources().getColor(R.color.blue_start_color),
                    getResources().getColor(R.color.blue_end_color));
            mAppConsumePowerContainer.setVisibility(View.GONE);
            mProblemContainer.setVisibility(View.GONE);
            mUsageTimeContainer.setVisibility(View.VISIBLE);
            mUsageTimeContainer.setAlpha(1);
            mBatteryContainer.setVisibility(View.VISIBLE);
            mBatteryContainer.setAlpha(1);
        }
    }

    private Runnable colorAnim = new Runnable() {
        @Override
        public void run() {

            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    mOptimizeContainer,
                    PropertyValuesHolder.ofFloat("scaleX", 1f, 1.05f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f, 1.05f)
            );
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //mOptimizeBtn.startAnim();
                }
            });
            objectAnimator.setInterpolator(new AccelerateInterpolator());
            objectAnimator.setDuration(1000);
            objectAnimator.start();


            int blueStartColor = getResources().getColor(R.color.blue_start_color);
            int redStartColor = getResources().getColor(R.color.red_start_color);
            int blueEndColor = getResources().getColor(R.color.blue_end_color);
            int redEndColor = getResources().getColor(R.color.red_end_color);
            PropertyValuesHolder startColor = PropertyValuesHolder.ofObject( "startColor", new ArgbEvaluator(), blueStartColor, redStartColor);
            PropertyValuesHolder endColor = PropertyValuesHolder.ofObject("endColor", new ArgbEvaluator(), blueEndColor, redEndColor);

            ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(startColor, endColor);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int startColor1 = (int) animation.getAnimatedValue("startColor");
                    int endColor1 = (int) animation.getAnimatedValue("endColor");
                    mRootView.setStartColorAndEndColor(startColor1, endColor1);
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHandler.post(foundProblem);
                }
            });

            valueAnimator.setDuration(3000);
            //valueAnimator1.setStartDelay(2000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
        }
    };


    private Runnable foundProblem = new Runnable() {
        @Override
        public void run() {
            Animator batteryAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.main_page_battery_anim);
            batteryAnim.setTarget(mBatteryContainer);
            batteryAnim.setDuration(200);
            batteryAnim.start();

            Animator usageTimeAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.main_page_battery_anim);
            usageTimeAnim.setTarget(mUsageTimeContainer);
            usageTimeAnim.setDuration(200);
            usageTimeAnim.start();

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(batteryAnim, usageTimeAnim);

            Animator foundProblemAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.main_page_found_problem_anim);
            foundProblemAnim.setTarget(mProblemContainer);
            foundProblemAnim.setDuration(400);

            final float density = getResources().getDisplayMetrics().density;
            mAppConsumePowerContainer.setAlpha(0);
            mAppConsumePowerContainer.setTranslationY(- 16 * density);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    mAppConsumePowerContainer.setAlpha(animatedFraction);
                    mAppConsumePowerContainer.setTranslationY((1 - animatedFraction) * (- 16 * density));
                }
            });
            valueAnimator.setDuration(400);

            AnimatorSet foundProblemSet = new AnimatorSet();
            foundProblemSet.play(foundProblemAnim).with(valueAnimator);

            AnimatorSet totalAnimSet = new AnimatorSet();
            totalAnimSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    Log.d(TAG, "onAnimationStart: " + apps.size());
                    mBatteryContainer.setVisibility(View.GONE);
                    mUsageTimeContainer.setVisibility(View.GONE);
                    mAppConsumePowerContainer.setAlpha(0);
                    mAppConsumePowerContainer.setVisibility(View.VISIBLE);
                    mAppConsumePowerContainer.setTranslationY(- 16 * density);
                    mProblemContainer.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mOptimizeText.setText(R.string.optimize);
                    mOptimizeText.setAlpha(0);
                    mHandler.postDelayed(showOptimize, 300);
                }
            });
            totalAnimSet.play(animatorSet).with(foundProblemSet);
            totalAnimSet.start();

        }
    };

    private Runnable showOptimize = new Runnable() {
        @Override
        public void run() {
            Animator animator = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.main_activity_optimize_show_in);
            animator.setTarget(mOptimizeText);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOptimizeBtn.startAnim();
                        }
                    }, 200);
                }
            });
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);
            animator.start();
        }
    };

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    @Override
    public void onBackPressed() {
        if (firstPressBack) {
            Toast.makeText(this, getResources().getString(R.string.press_again), Toast.LENGTH_SHORT).show();
            firstPressBack = false;
            mHandler.sendEmptyMessageDelayed(FIRST_PRESSED_BACK, 2000);
        } else {
            super.onBackPressed();
        }
    }

    private void registerReceiver() {
        mBatteryReceiver = new BatteryDataReceiver(this, this);
    }

    private void gotoPowerBoostPage() {
        try {
            String gpUrl = "https://play.google.com/store/apps/details?id=com.batterysaver.powerplus";
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(gpUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.vending");
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "gotoPowerBoostPage: web open abclauncher");
            String gpUrl = "https://play.google.com/store/apps/details?id=com.batterysaver.powerplus";
            Uri uri = Uri.parse(gpUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
            }catch (Exception exception) {
                Log.d(TAG, "gotoPowerBoostPage: "  + exception.getMessage());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBatteryReceiver.unregisterReceiver();
        mHandler.removeMessages(REFRESH_BATTERY);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        if (ACTION_CLEAN.equals(intent.getStringExtra("start"))){
            Intent intent1 = new Intent(getApplicationContext(), MemoryCleanActivity.class);
            startActivity(intent1);
        }else if (ACTION_CHARGE.equals(intent.getStringExtra("start"))){
            Intent intent1= new Intent(getApplicationContext(), ChargeActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if (mShowUsageTime && mPercent != 0) {
            mUsageTimeDes.setText(R.string.usage_time);
            String hour = Utils.getUsageHourValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            String mins = Utils.getUsageMinutesValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            mTimeLeftHourValue.setText(hour);
            mTimeLeftMinutesValue.setText(mins);
        }


        if (!SettingsHelper.getLockScreenOpened(getApplicationContext())){
            mChargeScreenTips.setVisibility(View.VISIBLE);
        }else {
            mChargeScreenTips.setVisibility(View.INVISIBLE);
        }

        if (SettingsHelper.getCleanTimes(getApplicationContext()) == 3 &&
                !SettingsHelper.getHasRateUs(getApplicationContext())) {
            RateUsFragment rateUsFragment = new RateUsFragment();
            rateUsFragment.show(getFragmentManager(), "rate_us");
            SettingsHelper.setHasRateUs(getApplicationContext(), true);
        }

        if (!CleanUtil.shouldCleanMemory(getApplicationContext())){
            mRootView.setStartColorAndEndColor(getResources().getColor(R.color.blue_start_color),
                    getResources().getColor(R.color.blue_end_color));
            mAppConsumePowerContainer.setVisibility(View.GONE);
            mProblemContainer.setVisibility(View.GONE);
            mUsageTimeContainer.setVisibility(View.VISIBLE);
            mUsageTimeContainer.setAlpha(1);
            mBatteryContainer.setVisibility(View.VISIBLE);
            mBatteryContainer.setAlpha(1);
        }
    }

    @Override
    public void receiveBatteryData(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        mPercent = LockerUtils.getBatteryPercent(level, scale);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        mBatteryProgress.setText(mPercent+"");
        mBpBatteryProgress.setProgress(mPercent);


        String hour = Utils.getUsageHourValue(Utils.getUsageTime(getApplicationContext(), mPercent));
        String mins = Utils.getUsageMinutesValue(Utils.getUsageTime(getApplicationContext(), mPercent));
       /* if (false) {
            mShowUsageTime = false;
            mUsageTimeDes.setText(R.string.charging_time);
            mTimeLeftHourValue.setText(LockerUtils.getCharingHourValueStr(getApplicationContext(), intent, mPercent));
            mTimeLeftMinutesValue.setText(LockerUtils.getCharingMinutesValueStr(getApplicationContext(), intent, mPercent));
        }else {*/
            mShowUsageTime = true;
            mUsageTimeDes.setText(R.string.usage_time);
            if (CleanUtil.shouldCleanMemory(getApplicationContext())){
                mTimeLeftHourValue.setText(hour);
                mTimeLeftMinutesValue.setText(mins);
            }
        //}
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case REFRESH_BATTERY:
                    if (!CleanUtil.shouldCleanMemory(getApplicationContext())) {
                        updateUsageTime();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_BATTERY, 1000);
                    break;
                case FIRST_PRESSED_BACK:
                    firstPressBack = true;
                    break;
            }
            return false;
        }


    });

    private void updateUsageTime() {
        if (mShowUsageTime) {
            Log.d(TAG, "updateUsageTime: ");
            String hour = Utils.getUsageHourValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            String mins = Utils.getUsageMinutesValue(Utils.getUsageTime(getApplicationContext(), mPercent));
            mUsageTimeDes.setText(R.string.usage_time);
            mTimeLeftHourValue.setText(hour);
            mTimeLeftMinutesValue.setText(mins);
        }
    }

}
