package com.zoro.roronoazorobanner.listener;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * author: leo on 2016/6/14 0014 14:13
 * email : leocheung4ever@gmail.com
 * description: page turning adapter
 * what & why is modified:
 */
public class ZoroPageChangeListener implements ViewPager.OnPageChangeListener {

    private ArrayList<ImageView> pointViews;
    private int[] pageIndicatorId;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    public ZoroPageChangeListener(ArrayList<ImageView> pointViews, int[] pageIndicatorId) {
        this.pointViews = pointViews;
        this.pageIndicatorId = pageIndicatorId;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < pointViews.size(); i++) {
            pointViews.get(position).setImageResource(pageIndicatorId[1]);
            if (position != i) {
                pointViews.get(position).setImageResource(pageIndicatorId[0]);
            }
        }
        if (onPageChangeListener != null)
            onPageChangeListener.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPageChangeListener != null)
            onPageChangeListener.onPageScrollStateChanged(state);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }
}
