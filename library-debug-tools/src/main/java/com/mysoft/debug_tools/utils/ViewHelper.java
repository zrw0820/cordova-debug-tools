package com.mysoft.debug_tools.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.mysoft.debug_tools.R;

/**
 * Created by Zourw on 2018/9/14.
 */
public class ViewHelper {
    public static int getStatusHeight() {
        int height = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = Resources.getSystem().getDimensionPixelSize(resourceId);
            if (height > 0) {
                return height;
            }
        }
        return height;
    }

    public static void setDefaultDivider(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        DividerItemDecoration divider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.divider_horizontal);
        if (drawable != null) {
            divider.setDrawable(drawable);
        }
        recyclerView.addItemDecoration(divider);
    }
}
