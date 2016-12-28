package com.abclauncher.powerboost.mode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.mode.util.AudioUtil;
import com.abclauncher.powerboost.mode.util.BluetoothUtil;
import com.abclauncher.powerboost.mode.util.HapticFeedbackUtil;
import com.abclauncher.powerboost.mode.util.MobileDataUtil;
import com.abclauncher.powerboost.mode.util.SyncUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sks on 2016/12/26.
 */

public class ModeActivity extends AppCompatActivity {
    private static final String TAG = "ModeActivity";
    @InjectView(R.id.battery_mode_check)
    ImageView mBatteryMode;
    @InjectView(R.id.general_mode_check)
    ImageView mGeneralMode;
    @InjectView(R.id.sleep_mode_check)
    ImageView mSleepMode;
    @InjectView(R.id.my_mode_check)
    ImageView mMyMode;

    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @OnClick(R.id.general_mode_menu)
    public void onGeneralModeMenuClicked(){
        Toast.makeText(this, "General Mode", Toast.LENGTH_SHORT).show();
        showGeneralModeDialog();
    }


    @OnClick(R.id.general_mode)
    public void onGeneralModeItemClicked(){
        initCurrentStatus(0);
    }

    @OnClick(R.id.battery_save_mode_menu)
    public void onBatterySavingModeMenuClicked(){
        Toast.makeText(this, "Battery Mode", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.battery_save_mode)
    public void onBatterySavingModeItemClicked(){
        initCurrentStatus(1);
    }

    @OnClick(R.id.sleep_mode_menu)
    public void onSleepModeMenuClicked(){
        Toast.makeText(this, "Sleep Mode", Toast.LENGTH_SHORT).show();

    }
    @OnClick(R.id.sleep_mode)
    public void onSleepModeItemClicked(){
        initCurrentStatus(2);
        MobileDataUtil.getInstance(getApplicationContext()).setMobileData(true);
    }

    @OnClick(R.id.my_mode_menu)
    public void onMyModeMenuClicked(){
        Toast.makeText(this, "My Mode", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.my_mode)
    public void onMyModeItemClicked(){
        initCurrentStatus(3);
        HapticFeedbackUtil.getInstance(getApplicationContext()).setHapticFeedbackEnable(false);
    }


    private int mCurrentSelectedPosition;
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

        initCurrentStatus(mCurrentSelectedPosition);
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

    private void showGeneralModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_general_mode, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
