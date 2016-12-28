package com.abclauncher.powerboost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.abclauncher.powerboost.bean.AppInfo;
import com.abclauncher.powerboost.util.Utils;
import com.abclauncher.powerboost.view.HorizontalProgress;
import com.abclauncher.powerboost.view.RecyclerViewDecoration;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sks on 2016/12/22.
 */

public class RankActivity extends AppCompatActivity{

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.checkbox)
    CheckBox mCheckBox;

    private List<AppInfo> appInfos;
    private Adapter mAdapter;


    @OnClick(R.id.back)
    public void finishActivity(){
        onBackPressed();
    }

    @OnClick(R.id.hide_system_spp)
    public void hideOrShowSystemApps(){
        mCheckBox.setChecked(!mCheckBox.isChecked());
        if (mCheckBox.isChecked()) {
            appInfos = Utils.getNonSystemAppList();
        }else {
            appInfos = Utils.getAllRunningApps();
        }
        //appInfos = Utils.getRunningAppList(getApplicationContext(), !mCheckBox.isChecked());
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_layout);
        ButterKnife.inject(this);

        if (mCheckBox.isChecked()) {
            appInfos = Utils.getNonSystemAppList();
        }else {
            appInfos = Utils.getAllRunningApps();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter();
        mRecyclerView.addItemDecoration(new RecyclerViewDecoration(getResources(),
                R.color.rank_activity_divider_color, R.dimen.divider_height, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

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
            holder.mStop.setImageResource(appInfo.isSystemApp ? R.drawable.ic_rank_view : R.drawable.ic_rank_stop);

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
            public ImageView mStop;
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
