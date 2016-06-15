package com.zoro.roronoazorobanner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * author: leo on 2016/6/14 0014 13:24
 * email : leocheung4ever@gmail.com
 * description: viewpager scroller
 */
public class ViewPagerScroller extends Scroller {

    private int mScrollDuration = 800; //滑动速度  值越大滑动越慢
    private boolean zero; //是否设置滑动持续时间为0


    //===================================== Constructors ========================================//

    public ViewPagerScroller(Context context) {
        super(context);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    //==================================== Override Method =======================================//

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
    }


    //===================================== Getter Setter ========================================//

    public int getScrollDuration() {
        return mScrollDuration;
    }

    public void setScrollDuration(int mScrollDuration) {
        this.mScrollDuration = mScrollDuration;
    }

    public boolean isZero() {
        return zero;
    }

    public void setZero(boolean zero) {
        this.zero = zero;
    }
}
