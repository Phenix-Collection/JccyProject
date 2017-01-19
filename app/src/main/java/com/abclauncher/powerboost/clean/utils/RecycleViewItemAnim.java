package com.abclauncher.powerboost.clean.utils;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;


/**
 * Created by shenjinliang on 16/12/26.
 */

public class RecycleViewItemAnim extends SimpleItemAnimator {

    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();

    private ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();

    protected ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    protected ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();

    protected Interpolator mInterpolator = new LinearInterpolator();

    private static class MoveInfo {

        public RecyclerView.ViewHolder holder;
        public int fromX, fromY, toX, toY;

        private MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class ChangeInfo {

        public RecyclerView.ViewHolder oldHolder, newHolder;
        public int fromX, fromY, toX, toY;

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        private ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX,
                           int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        @Override
        public String toString() {
            return "ChangeInfo{" +
                    "oldHolder=" + oldHolder +
                    ", newHolder=" + newHolder +
                    ", fromX=" + fromX +
                    ", fromY=" + fromY +
                    ", toX=" + toX +
                    ", toY=" + toY +
                    '}';
        }
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        mPendingRemovals.add(holder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        mPendingAdditions.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
//        MoveInfo info = new MoveInfo(holder, fromX, fromY, toX, toY);
//        mPendingMoves.add(info);
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        if(!mPendingRemovals.isEmpty()){
            for(final RecyclerView.ViewHolder holder : mPendingRemovals ){
                animateRemoveIml(holder);
            }
        }

        if(!mPendingAdditions.isEmpty()){
            for(final RecyclerView.ViewHolder holder : mPendingAdditions ){
                animateAddIml(holder);
            }
        }
    }

    private void animateRemoveIml(final RecyclerView.ViewHolder holder){
        View view = holder.itemView;
        ViewCompat.animate(holder.itemView)
                .alpha(0)
                .scaleX(0)
                .scaleY(0)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .start();
    }

    private void animateAddIml(final RecyclerView.ViewHolder holder){
        View view = holder.itemView;
        ViewCompat.setAlpha(view, 0);
        ViewCompat.animate(holder.itemView)
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)

                .start();
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return  (!mPendingAdditions.isEmpty() || !mPendingRemovals.isEmpty());
    }
}
