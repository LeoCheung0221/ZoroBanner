package com.zoro.roronoazorobanner.adapetr;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.zoro.roronoazorobanner.R;
import com.zoro.roronoazorobanner.holder.Holder;
import com.zoro.roronoazorobanner.holder.ZoroViewHolderCreator;
import com.zoro.roronoazorobanner.view.ZoroLoopViewPager;

import java.util.List;

/**
 * author: leo on 2016/6/14 0014 10:23
 * email : leocheung4ever@gmail.com
 * description: adapter for viewpager
 * what & why is modified:
 */
public class ZoroPagerAdapter<T> extends PagerAdapter {

    private static final int MULTIPLE_COUNT = 300;

    private List<T> mDatas;
    private boolean isLoop = true;

    private ZoroLoopViewPager viewPager;
    private ZoroViewHolderCreator holderCreator;


    //=================================== Constructor =======================================//

    public ZoroPagerAdapter(ZoroViewHolderCreator holderCreator, List<T> dataList) {
        this.holderCreator = holderCreator;
        this.mDatas = dataList;
    }


    //=================================== Override Methods =======================================//

    /**
     * 返回要滑动的view的个数
     */
    @Override
    public int getCount() {
        return isLoop ? getDataSize() * MULTIPLE_COUNT : getDataSize();
    }

    /**
     * 将当前视图添加到container中  病返回当前view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int presentPosition = getPresentPosition(position);
        View view = getView(presentPosition, null, container);
        container.addView(view);
        return view;
    }

    /**
     * 当前的页面是否与给定的键相关联
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 从当前container中删除指定位置的view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFirstItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
        }
    }

    //====================================== Public Methods ======================================//

    /**
     * 根据数据容量大小 取模计算可循环展示的位置
     */
    public int getPresentPosition(int position) {
        int dataSize = getDataSize();
        if (dataSize == 0)
            return 0;
        int presentPos = position % dataSize;
        return presentPos;
    }

    /**
     * 获取数据源大小
     */
    public int getDataSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public View getView(int position, View view, ViewGroup container) {
        Holder holder;
        if (view == null) {
            holder = (Holder) holderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(R.id.zoro_item_tag, holder);
        } else {
            holder = (Holder<T>) view.getTag(R.id.zoro_item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty())
            holder.UpdateUI(container.getContext(), position, mDatas.get(position));
        return view;
    }


    //=================================== Getter Setter =======================================//

    public boolean isLoop() {
        return isLoop;
    }

    /**
     * 设置可以循环播放
     */
    public void setCanLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public ZoroLoopViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 设置ViewPager
     */
    public void setViewPager(ZoroLoopViewPager viewPager) {
        this.viewPager = viewPager;
    }

}
