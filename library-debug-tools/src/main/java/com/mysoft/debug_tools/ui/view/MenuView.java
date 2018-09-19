package com.mysoft.debug_tools.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.mysoft.debug_tools.utils.DimenConvert;

/**
 * Created by Zourw on 2018/9/19.
 */
public class MenuView extends FrameLayout implements View.OnClickListener {
    private View menuBtn;

    private int menuSpace;
    private boolean menuOpen;

    public MenuView(Context context) {
        super(context);
        init();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        menuSpace = DimenConvert.dp2px(10);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuBtn = getChildAt(0);
        menuBtn.setOnClickListener(this);
    }

    /*@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int menuWidth = menuBtn.getMeasuredWidth();
        int menuHeight = menuBtn.getMeasuredHeight();
        menuBtn.layout(right - menuWidth, bottom - menuHeight, right, bottom);

        int t = menuBtn.getTop();
        for (int i = 1; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int l = menuBtn.getLeft() - (menuWidth - child.getMeasuredWidth()) / 2;
            t -= menuSpace + child.getMeasuredHeight();
            child.layout(l, t, l + child.getMeasuredWidth(), t + child.getMeasuredHeight());
            child.setTranslationY(menuBtn.getTop() + (menuHeight - child.getMeasuredHeight()) / 2 - child.getTop());
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v == menuBtn) {
            menuOpen = !menuOpen;
            for (int i = 1, count = getChildCount(); i < count; i++) {
                final View child = getChildAt(i);
                int beginY = child.getTop();
                int endY = menuBtn.getTop() + (menuBtn.getMeasuredHeight() - child.getMeasuredHeight()) / 2;
                animation(child, endY - beginY);
            }
        }
    }

    private void animation(final View child, int distance) {
        ValueAnimator translateAnim;
        ValueAnimator rotateAnim;
        if (menuOpen) {
            translateAnim = ValueAnimator.ofInt(distance, 0);
            rotateAnim = ValueAnimator.ofInt(0, -180);
        } else {
            translateAnim = ValueAnimator.ofInt(0, distance);
            rotateAnim = ValueAnimator.ofInt(180, 360);
        }
        translateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                child.setTranslationY(value);
            }
        });
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                child.setRotation((int) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new LinearInterpolator());
        set.setDuration(200);
        set.playTogether(translateAnim, rotateAnim);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                child.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!menuOpen) {
                    child.setVisibility(INVISIBLE);
                }
            }
        });
        set.start();
    }
}
