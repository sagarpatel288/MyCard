package com.creativeconflux.srdp.mycard.ui.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.creativeconflux.srdp.mycard.listeners.NestedScrollViewScrollStateListener;

public class MyNestedScrollView extends NestedScrollView {

    private int mState = RecyclerView.SCROLL_STATE_IDLE;

    public void setScrollListener(NestedScrollViewScrollStateListener scrollListener) {
        this.mScrollListener = scrollListener;
    } 
 
    private NestedScrollViewScrollStateListener mScrollListener;
 
    public MyNestedScrollView(Context context) {
        super(context);
    } 
 
    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    } 
 
    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    } 
 
    @Override 
    public void stopNestedScroll() {
        super.stopNestedScroll();
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE);
    } 
 
    @Override 
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        super.onStartNestedScroll(child, target, nestedScrollAxes);
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING);
        return true;
    } 

    @Override 
    public boolean startNestedScroll(int axes) {
        super.startNestedScroll(axes);
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING); 
        return true;
    } 

    private void dispatchScrollState(int state) {
        if (mScrollListener != null) {
            mScrollListener.onNestedScrollViewStateChanged(state);
            mState = state;
        } 
    }
} 