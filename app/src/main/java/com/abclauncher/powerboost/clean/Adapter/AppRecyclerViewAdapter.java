package com.abclauncher.powerboost.clean.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.clean.bean.AppProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenjinliang on 16/12/26.
 */

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<AppProcessInfo> mDatas = new ArrayList<>();
    private Context mContext;

    public AppRecyclerViewAdapter(Context context, List<AppProcessInfo> datas){
        mDatas = datas;
        mContext = context;
    }

    public void addItem(AppProcessInfo info){
        if(mDatas == null){
            mDatas = new ArrayList<>();
        }
        mDatas.add(info);
        notifyItemInserted(mDatas.size() -1 );
    }

    public void removeItem(){
        if(mDatas != null && mDatas.size() != 0){
            notifyItemRemoved(mDatas.size() -1);
            mDatas.remove(mDatas.size() -1);
        }
    }

    public void removeItem(int count){
        for (int i = 0; i < count; i++) {
            notifyItemRemoved(0);
            mDatas.remove(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.memory_clean_recycler_item,parent,false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AppViewHolder) holder).icon.setImageDrawable(mDatas.get(position).icon);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        public AppViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.memory_clean_app_icon);
        }
    }
}
