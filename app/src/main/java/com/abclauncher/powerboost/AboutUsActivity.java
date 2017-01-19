package com.abclauncher.powerboost;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sks on 2017/1/13.
 */

public class AboutUsActivity extends BaseActivity {

    @InjectView(R.id.app_version)
    TextView mVersion;

    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_layout);

        ButterKnife.inject(this);

        mVersion.setText(getResources().getString(R.string.version) + " " + getVersionName());
    }

    private String getVersionName(){
        PackageInfo packageInfo = null;
        String versionName = "1.0";
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }
}
