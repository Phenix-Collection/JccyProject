package com.abclauncher.powerboost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.abclauncher.powerboost.mode.ModeActivity;
import com.abclauncher.powerboost.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.menu_container)
    View mMenuContainer;

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

    @OnClick(R.id.white_list)
    public void openWhiteListSettingActivity(){
        hideMenu();
        Toast.makeText(this, "White List", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.settings)
    public void openSettingsActivity(){
        hideMenu();
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.share)
    public void onShareItemClicked(){
        hideMenu();
        Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.rate)
    public void onRateItemClicked(){
        hideMenu();
        Toast.makeText(this, "Rate", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.feedback)
    public void onFeedbackItemClicked(){
        hideMenu();
        Toast.makeText(this, "FeedBack", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.about_us)
    public void onAboutUsItemClicked(){
        hideMenu();
        Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.rank)
    public void onRankClicked(){
        Intent intent = new Intent(this, RankActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.mode)
    public void onModeClicked(){
        Intent intent = new Intent(this, ModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private Runnable mInitAllRunningApps =  new Runnable() {
        @Override
        public void run() {
            Utils.initAllRunningApps(getApplicationContext());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(mInitAllRunningApps).start();
    }
}
