package com.zoro.roronoazorobanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zoro.roronoazorobanner.adapetr.ZoroPagerAdapter;
import com.zoro.roronoazorobanner.holder.ZoroViewHolderCreator;
import com.zoro.roronoazorobanner.listener.OnItemClickListener;
import com.zoro.roronoazorobanner.listener.ZoroPageChangeListener;
import com.zoro.roronoazorobanner.view.ZoroLoopViewPager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * author: leo on 2016/6/14 0014 09:03
 * email : leocheung4ever@gmail.com
 * description: advertisement banner
 */
public class ZoroBanner<T> extends LinearLayout {

    private List<T> mDatas;
    private boolean isLoop = true;
    private ZoroLoopViewPager viewPager;
    private ViewGroup llPageIndicator;
    private ViewPagerScroller scroller;
    private boolean turning;
    private AdSwitchTask adSwitchTask;
    private long autoTurningTime;
    private ZoroPagerAdapter pageAdapter;
    private int[] pageIndicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private ZoroPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private boolean canTurn = false;

    private enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }


    //========================================= Constructor ======================================//

    public ZoroBanner(Context context) {
        this(context, null);
    }

    public ZoroBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoroBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZoroBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZoroBanner);
        isLoop = ta.getBoolean(R.styleable.ZoroBanner_isLoop, true); //默认是可以无限循环的
        ta.recycle(); //不回收将会影响下一次设置
        init(context);
    }


    //====================================== Override Methods =====================================//

    /**
     * 父Banner控件需要分发事件
     * 触摸控件的时候 翻页应该停止
     * 离开界面的时候 如果开启了翻页 则重新启动翻页
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_OUTSIDE) {

            if (canTurn) startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {

            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }


    //====================================== Private Methods =====================================//

    private void init(Context context) {
        View zView = LayoutInflater.from(context).inflate(R.layout.viewpager_with_indicator, this, true);
        viewPager = (ZoroLoopViewPager) zView.findViewById(R.id.zoroLoopViewPager);
        llPageIndicator = (ViewGroup) zView.findViewById(R.id.llPageIndicator);
        initViewPagerScroll();

        adSwitchTask = new AdSwitchTask(this);
    }

    /**
     * 初始化ViewPager滑动 设置滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //========================================= Inner Class ======================================//

    /**
     * 新启线程 -- 广告切换任务
     */
    static class AdSwitchTask implements Runnable {
        //定义弱引用 防止内存泄漏
        private final WeakReference<ZoroBanner> referenceBanner;

        AdSwitchTask(ZoroBanner zoroBanner) {
            this.referenceBanner = new WeakReference<>(zoroBanner);
        }

        @Override
        public void run() {
            ZoroBanner zoroBanner = referenceBanner.get();

            if (zoroBanner != null) {
                if (zoroBanner.viewPager != null && zoroBanner.turning) {
                    int page = zoroBanner.viewPager.getCurrentItem() + 1;
                    zoroBanner.viewPager.setCurrentItem(page);
                    zoroBanner.postDelayed(zoroBanner.adSwitchTask, zoroBanner.autoTurningTime);
                }
            }
        }
    }


    //======================================= Public Methods =====================================//

    /**
     * 设置ViewPager 包括数据适配 是否循环开启
     *
     * @param holderCreator
     * @param dataList
     * @return
     */
    public ZoroBanner setPages(ZoroViewHolderCreator holderCreator, List<T> dataList) {
        this.mDatas = dataList;
        pageAdapter = new ZoroPagerAdapter(holderCreator, mDatas);
        viewPager.setAdapter(pageAdapter, isLoop);

        if (pageIndicatorId != null) {
            setPageIndicators(pageIndicatorId);
        }
        return this;
    }

    /**
     * 通知数据变化
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (pageIndicatorId != null) {
            setPageIndicators(pageIndicatorId);
        }
    }

    /**
     * 设置底部指示器的可见性
     *
     * @param visible
     * @return
     */
    public ZoroBanner setPageIndicatorVisible(boolean visible) {
        llPageIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置底部指示器资源图片
     */
    private ZoroBanner setPageIndicators(int[] pageIndicatorId) {
        this.pageIndicatorId = pageIndicatorId;
        llPageIndicator.removeAllViews();
        mPointViews.clear();

        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(pageIndicatorId[1]);
            else
                pointView.setImageResource(pageIndicatorId[0]);
            mPointViews.add(pointView); //将指示器图片加入到ImageView控件中
            llPageIndicator.addView(pointView); //将ImageView控件加入到ViewGroup中 进行展示
        }
        pageChangeListener = new ZoroPageChangeListener(mPointViews, pageIndicatorId);
        viewPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getPresentItem());
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 设置指示器的布局位置
     *
     * @param align
     * @return
     */
    public ZoroBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llPageIndicator.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        llPageIndicator.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 开启翻页功能
     *
     * @param autoTurningTime
     * @return
     */
    public ZoroBanner startTurning(long autoTurningTime) {
        if (turning)
            stopTurning();
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 设置页面切换动画效果
     *
     * @param transformer
     * @return
     */
    public ZoroBanner setPageTransformer(ViewPager.PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 设置页面切换监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public ZoroBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器 将默认的监听器注入到用户设置的监听器上
        //否则 直接使用用户设置的监听器
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else
            viewPager.addOnPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     * @return
     */
    public ZoroBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnItemClickListener(null);
            return this;
        }
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    //==================================== Getter Setter =========================================//

    /**
     * 是否正在切换页面
     *
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /**
     * 获取页面切换监听器
     *
     * @return
     */
    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 获取当前的页面position
     *
     * @return
     */
    public int getCurrentItem() {
        if (viewPager != null)
            return viewPager.getPresentItem();
        return -1;
    }

    /**
     * 设置当前的页面index
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (viewPager != null)
            viewPager.setCurrentItem(position);
    }

    /**
     * 设置ViewPager的滚动时间 时间越长 滚动越慢
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    /**
     * 获得ViewPager的滚动时间
     *
     * @return
     */
    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    /**
     * 设置循环
     *
     * @param isLoop
     */
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        viewPager.setCanLoop(isLoop);
    }

    /**
     * 获得ViewPager对象
     *
     * @return
     */
    public ZoroLoopViewPager getViewPager() {
        return viewPager;
    }
}
