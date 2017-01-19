package com.abclauncher.powerboost.clean.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.abclauncher.powerboost.R;

import java.util.List;

/**
 * Created by shenjinliang on 16/12/26.
 */

public class AppGridViewAdapter extends BaseAdapter {
    private List<Integer> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;


    public AppGridViewAdapter(Context context, List<Integer> list){
        mDatas = list;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.memory_clean_recycler_item, parent,
                    false);
            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.memory_clean_app_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIcon.setImageResource(mDatas.get(position));
        return convertView;
    }

    private final class ViewHolder{
        ImageView mIcon;
    }
}
