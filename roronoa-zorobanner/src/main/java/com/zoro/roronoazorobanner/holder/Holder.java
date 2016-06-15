package com.zoro.roronoazorobanner.holder;

import android.content.Context;
import android.view.View;

/**
 * author: leo on 2016/6/14 0014 12:29
 * email : leocheung4ever@gmail.com
 */
public interface Holder<T> {
    View createView(Context context);
    void UpdateUI(Context context, int position, T data);
}
