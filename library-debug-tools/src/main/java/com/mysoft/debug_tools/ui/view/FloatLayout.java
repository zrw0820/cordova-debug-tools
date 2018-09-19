package com.mysoft.debug_tools.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.mysoft.debug_tools.utils.DimenConvert;

/**
 * Created by Zourw on 2018/9/19.
 */
public class FloatLayout extends ViewGroup implements View.OnClickListener {
    private View mContentView;
    private View mActionView;

    private ViewDragHelper mDragHelper;
    private Point mFinalPoint = new Point();

    private int margin;
    private boolean isLayout;

    private int menuSpace;
    private boolean menuOpen;

    public FloatLayout(Context context) {
        super(context);
        init();
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 10f, new DragCallback());
        margin = DimenConvert.dp2px(10);
        menuSpace = DimenConvert.dp2px(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
        mActionView = getChildAt(1);
        mActionView.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int actionWidth = mActionView.getMeasuredWidth();
        int actionHeight = mActionView.getMeasuredHeight();

        if (!isLayout) {
            mFinalPoint.x = r - l - actionWidth - margin;
            mFinalPoint.y = b - t - actionHeight - DimenConvert.dp2px(40);
            isLayout = true;
        }

        mContentView.layout(l, t, r, b);
        mActionView.layout(mFinalPoint.x, mFinalPoint.y, mFinalPoint.x + actionWidth, mFinalPoint.y + actionHeight);

        int top = mFinalPoint.y;
        for (int i = 2, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            child.setVisibility(INVISIBLE);
            int left = mFinalPoint.x - (actionWidth - child.getMeasuredWidth()) / 2;
            top -= menuSpace + child.getMeasuredHeight();
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            child.setTranslationY(mFinalPoint.y + (actionHeight - child.getMeasuredHeight()) / 2 - child.getTop());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mActionView) {
            menuOpen = !menuOpen;
            int beginY = mActionView.getTop();
            for (int i = 2, count = getChildCount(); i < count; i++) {
                final View child = getChildAt(i);
                beginY += menuSpace + child.getMeasuredHeight();
                int endY = mActionView.getTop() + (mActionView.getMeasuredHeight() - child.getMeasuredHeight()) / 2;
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

    public void showActionView(boolean show) {
        if (mActionView != null) {
            mActionView.setVisibility(show ? VISIBLE : GONE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class DragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mActionView;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - child.getWidth() - leftBound;
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - child.getHeight() - topBound;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            for (int i = 2; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.setTranslationX(child.getTranslationX() + dx);
                child.setTranslationY(child.getTranslationY() + dy);
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild == mActionView) {
                int finalLeft = margin;
                if (releasedChild.getLeft() + releasedChild.getWidth() / 2 > getWidth() / 2) {
                    finalLeft = getWidth() - releasedChild.getWidth() - margin;
                }

                mFinalPoint.set(finalLeft, releasedChild.getTop());

                if (mDragHelper.settleCapturedViewAt(mFinalPoint.x, mFinalPoint.y)) {
                    ViewCompat.postInvalidateOnAnimation(FloatLayout.this);
                }
            }
        }
    }
}