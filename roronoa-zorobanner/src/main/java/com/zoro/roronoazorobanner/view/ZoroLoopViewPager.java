package com.zoro.roronoazorobanner.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zoro.roronoazorobanner.adapetr.ZoroPagerAdapter;
import com.zoro.roronoazorobanner.listener.OnItemClickListener;

/**
 * author: leo on 2016/6/14 0014 10:18
 * email : leocheung4ever@gmail.com
 * description: ViewPager which enable loop
 */
public class ZoroLoopViewPager extends ViewPager {

    private OnItemClickListener mOnItemClickListener;
    private OnPageChangeListener mOnPageChangeListener;

    private ZoroPagerAdapter mAdapter;
    private boolean isCanScroll = true;

    private float oldX = 0;
    private float newX = 0;
    private static final float sensitiveX = 5;
    private boolean isLoop = true;


    //====================================  Constructors  ========================================//

    public ZoroLoopViewPager(Context context) {
        this(context, null);
    }

    public ZoroLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //==================================  Override Methods  ======================================//

    /*
     * 是否消费触摸事件
     * 如果处于可滑动状态 --> 如果被点击 --> 获得手势按下和抬起的距离  --> 如果距离之差小于某个敏感临界值 -->认为点击事件触发 -->值重置
     *                    --> 不管消费与否, 最终都返回是否触摸事件的boolean值
     * 否则 --> 不消费触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            if (mOnItemClickListener != null) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = ev.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        newX = ev.getX();
                        if (Math.abs(oldX - newX) < sensitiveX) {
                            mOnItemClickListener.onItemClick(getPresentItem());
                        }
                        oldX = 0;
                        newX = 0;
                        break;
                }
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    /*
     * 当前ViewPager控件是否拦截触摸事件
     * 如果处于可滑动状态 --> 拦截下来供给ViewPager进行消费事件
     * 否则 --> 不拦截继续向下(ViewPager->ImageView)传递事件直至被拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    //=================================== Private Methods ========================================//

    /**
     * 初始化控件
     */
    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);
    }

    /**
     * 页面滑动变化 监听器
     */
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private int mPreviousPosition = -1;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int presentPosition = position;
            if (mOnPageChangeListener != null) {
                //如果当前位置不等于数据容量大小 则进行下一轮页面滑动切换
                //否则 滑动偏移量小于0.5 , 回到当前页面 ; 大于0.5 进入下一页面/前一页面
                if (presentPosition != mAdapter.getDataSize() - 1) {
                    mOnPageChangeListener.
                            onPageScrolled(presentPosition, positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        mOnPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        mOnPageChangeListener.onPageScrolled(presentPosition, 0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            int presentPosition = mAdapter.getPresentPosition(position);
            //如果用于区别点击哪一个页面
            if (mPreviousPosition != presentPosition) {
                mPreviousPosition = presentPosition;
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(presentPosition);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };


    //=================================== Getter Setter ==========================================//

    public ZoroPagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置适配器
     *
     * @param adapter 里式替换原则
     * @param isLoop  是否可以循环
     */
    public void setAdapter(PagerAdapter adapter, boolean isLoop) {
        mAdapter = (ZoroPagerAdapter) adapter;
        mAdapter.setCanLoop(isLoop);
        mAdapter.setViewPager(this);
        super.setAdapter(mAdapter);

        setCurrentItem(getFirstItem(), false);
    }

    /**
     * 是否可以滑动
     */
    public boolean isCanScroll() {
        return isCanScroll;
    }

    /**
     * 设置是否可以滑动
     */
    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    /**
     * 获得当前的点击项
     *
     * @return int 点击项位置
     */
    public int getPresentItem() {
        return mAdapter != null ? mAdapter.getPresentPosition(super.getCurrentItem()) : 0;
    }

    /**
     * 获得第一项 是否循环
     */
    public int getFirstItem() {
        return isLoop ? mAdapter.getDataSize() : 0;
    }

    public int getLastItem() {
        return mAdapter.getDataSize() - 1;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setCanLoop(boolean isLoop) {
        this.isLoop = isLoop;
        if (isLoop == false)
            setCurrentItem(getPresentItem(), false);
        if (mAdapter == null)
            return;
        mAdapter.setCanLoop(isLoop);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //==================================  Public Methods  ========================================//


}
