package com.abclauncher.powerboost;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.abclauncher.powerboost.bean.AppInfo;
import com.abclauncher.powerboost.rank.RankDao;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.Utils;
import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;
import com.abclauncher.powerboost.view.HorizontalProgress;
import com.abclauncher.powerboost.view.RecyclerViewDecoration;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sks on 2016/12/22.
 */

public class RankActivity extends BaseActivity implements RankDao.AllAppsLoadedListener {

    private static final String TAG = "RankActivity";
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.content_view)
    View mContentView;
    @InjectView(R.id.loading_layout)
    View mLoadingLayout;

    @InjectView(R.id.checkbox)
    CheckBox mCheckBox;

    private List<AppInfo> appInfos;
    private Adapter mAdapter;
    private ProgressDialog mProgressDialog;


    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @OnClick(R.id.hide_system_spp)
    public void hideOrShowSystemApps(){
        mCheckBox.setChecked(!mCheckBox.isChecked());
        if (mCheckBox.isChecked()) {
            appInfos = RankDao.getInstance(getApplicationContext()).getNonSystemAppList();
        }else {
            appInfos = RankDao.getInstance(getApplicationContext()).getAllRunningAppsByCache();
        }
        mAdapter.notifyDataSetChanged();
        SettingsHelper.setShouldShowSystemApps(getApplicationContext(), !mCheckBox.isChecked());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_layout);
        ButterKnife.inject(this);
        // 设置右滑动返回
        RankDao rankDao = RankDao.getInstance(this);
        rankDao.setAppAppsLoadedListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = RankDao.getInstance(getApplicationContext()).initRunningAppList(getApplicationContext(),
                        SettingsHelper.getShouldShowSystemApps(getApplicationContext()));
            }
        }).start();

        mCheckBox.setChecked(!SettingsHelper.getShouldShowSystemApps(getApplicationContext()));



        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter();
        mRecyclerView.addItemDecoration(new RecyclerViewDecoration(getResources(),
                R.color.rank_activity_divider_color, R.dimen.divider_height, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void setStatusBar() {
        //super.setStatusBar();
        StatusBarUtil.setTransparent(this);
    }

    @Override
    public void onAllAppsInited() {
        Log.d(TAG, "onAllAppsInited: ");


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Animator animator = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.clean_content_dismiss_anim);
                animator.setTarget(mLoadingLayout);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mLoadingLayout.setVisibility(View.INVISIBLE);
                    }
                });
                animator.setDuration(50);
                animator.start();

                Animator appearAnim = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.rank_content_appear_anim);
                appearAnim.setTarget(mContentView);
                appearAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mContentView.setVisibility(View.VISIBLE);
                    }
                });
                appearAnim.setDuration(50);
                appearAnim.start();
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RankDao.getInstance(getApplicationContext()).cleanData();
    }

    private Handler mHandler = new Handler();

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.rank_activity_item, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final AppInfo appInfo = appInfos.get(position);
            holder.mAppName.setText(appInfo.appName);
            holder.mIcon.setImageDrawable(appInfo.icon);
            holder.mPercent.setText(appInfo.percent + "%");

            //进度条
            float progress = Float.valueOf(appInfo.percent);
            if (progress < 1f)  progress = 1f;
            holder.mProgress.setProgress(progress);

            //左侧按钮
            holder.mStop.setText(appInfo.isSystemApp ? R.string.view : R.string.stop);

            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openAppDetails(getApplicationContext(), appInfo.pkgName);
                }
            });
        }

        @Override
        public int getItemCount() {
            return appInfos == null ? 0 : appInfos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            @InjectView(R.id.app_name)
            public TextView mAppName;
            @InjectView(R.id.percent)
            public TextView mPercent;
            @InjectView(R.id.icon)
            public ImageView mIcon;
            @InjectView(R.id.stop)
            public TextView mStop;
            @InjectView(R.id.progress)
            public HorizontalProgress mProgress;
            @InjectView(R.id.root_view)
            public View mRootView;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }
    }

}
