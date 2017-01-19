package com.abclauncher.powerboost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sks on 2017/1/5.
 */

public class SplashActivity extends BaseActivity{

    @InjectView(R.id.logo)
    View mLogo;

    @InjectView(R.id.app_name)
    View mAppName;

    @InjectView(R.id.app_des)
    View mAppDes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);
        ButterKnife.inject(this);

        ObjectAnimator logo = ObjectAnimator.ofFloat(mLogo, "alpha", 0.5f, 1);
        logo.setDuration(200);
        ObjectAnimator appName = ObjectAnimator.ofFloat(mAppName, "alpha", 0.5f, 1);
        appName.setDuration(80);
        ObjectAnimator appDes = ObjectAnimator.ofFloat(mAppDes, "alpha", 0.5f, 1);
        appName.setDuration(50);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLogo.setVisibility(View.VISIBLE);
                mLogo.setAlpha(0);
                mAppDes.setVisibility(View.VISIBLE);
                mAppName.setVisibility(View.VISIBLE);
                mAppDes.setAlpha(0);
                mAppName.setAlpha(0);

            }
        });
        animatorSet.playSequentially(logo, appName, appDes);
        animatorSet.setDuration(330);
        animatorSet.setStartDelay(0);
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

}
