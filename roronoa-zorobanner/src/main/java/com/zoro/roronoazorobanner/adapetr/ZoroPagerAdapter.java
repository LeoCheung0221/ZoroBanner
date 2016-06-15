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
     * 返回当前滑动视图的个数
     *
     * @return
     */
    @Override
    public int getCount() {
        return isLoop ? getDataSize() * MULTIPLE_COUNT : getDataSize();
    }

    /**
     * 创建指定为位置的视图
     *
     * @param container
     * @param position
     * @return 返回一个代表新增页面视图的Object(key)
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int presentPosition = getPresentPosition(position);
        View view = getView(presentPosition, null, container);
        container.addView(view);
        return view;
    }

    /**
     * 判断返回的key值与一个页面的视图是否是代表同一个
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 从container中移除给定位置的view
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    /**
     * 返回是否已完成视图与key值得匹配
     *
     * @param container
     */
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
     * 获得当前位置的索引 取模计算值作为循环播放位置
     *
     * @param position
     * @return
     */
    public int getPresentPosition(int position) {
        int dataSize = getDataSize();
        if (dataSize == 0)
            return 0;
        int presentPos = position % dataSize;
        return presentPos;
    }

    /**
     * 获得数据容量大小�
     *
     * @return
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
     * 设置是否循环播放
     *
     * @param isLoop
     */
    public void setCanLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public ZoroLoopViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 设置ViewPager
     *
     * @param viewPager
     */
    public void setViewPager(ZoroLoopViewPager viewPager) {
        this.viewPager = viewPager;
    }

}
