package com.mysoft.debug_tools.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * DimenConvert
 * <p/>
 * Created by ${Zourw} on 2016/7/25.
 */
public class DimenConvert {
    /**
     * 获取DisplayMetrics对象
     */
    public static DisplayMetrics displayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        return displayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        return displayMetrics().heightPixels;
    }

    /**
     * dp转px
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, displayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, displayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(float pxVal) {
        return (pxVal / displayMetrics().density);
    }

    /**
     * px转sp
     */
    public static float px2sp(float pxVal) {
        return (pxVal / displayMetrics().scaledDensity);
    }
}
