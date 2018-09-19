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

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.utils.DimenConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zourw on 2018/9/19.
 */
public class FloatMenuLayout extends ViewGroup implements View.OnClickListener {
    private View contentView;
    private View menuView;
    private List<View> itemViews = new ArrayList<>();

    private ViewDragHelper dragHelper;
    private Point finalPoint = new Point();

    private int margin;
    private int space;

    private boolean animIsPlaying;
    private boolean openMenu;
    private boolean directionUp = true;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public FloatMenuLayout(Context context) {
        this(context, null);
    }

    public FloatMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, 10f, new DragCallback());
        margin = DimenConvert.dp2px(10);
        space = DimenConvert.dp2px(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        contentView = findViewById(R.id.content);
        menuView = findViewById(R.id.menu_action);

        String menuItemTag = getResources().getString(R.string.menu_item_tag);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (menuItemTag.equals(child.getTag())) {
                itemViews.add(child);
            }
        }

        for (final View itemView : itemViews) {
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onMenuClick(v, itemViews.indexOf(itemView));
                    }
                }
            });
        }

        menuView.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int menuWidth = menuView.getMeasuredWidth();
            int menuHeight = menuView.getMeasuredHeight();

            if (finalPoint.equals(0, 0)) {
                finalPoint.x = r - l - menuWidth - margin;
                finalPoint.y = b - t - menuHeight - DimenConvert.dp2px(40);
            }

            contentView.layout(l, t, r, b);
            menuView.layout(finalPoint.x, finalPoint.y, finalPoint.x + menuWidth, finalPoint.y + menuHeight);

            for (View itemView : itemViews) {
                itemView.setVisibility(GONE);
                int itemWidth = itemView.getMeasuredWidth();
                int itemHeight = itemView.getMeasuredHeight();
                int left = finalPoint.x + (menuWidth - itemWidth) / 2;
                int top = finalPoint.y + (menuHeight - itemHeight) / 2;
                itemView.layout(left, top, left + itemWidth, top + itemHeight);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == menuView && !animIsPlaying && !itemViews.isEmpty()) {
            if (openMenu) {
                menuItemAnim(itemViews.size() - 1, false);
                v.animate()
                        .rotation(v.getRotation() - 90)
                        .setDuration(200)
                        .start();
            } else {
                menuItemAnim(0, true);
                v.animate()
                        .rotation(v.getRotation() + 90)
                        .setDuration(200)
                        .start();
            }
        }
    }

    private void menuItemAnim(final int index, final boolean expand) {
        View view = itemViews.get(index);
        float translateY = view.getHeight() + space;

        float flag = directionUp ? (expand ? -1f : 1f) : (expand ? 1f : -1f);

        ValueAnimator translateAnim = ValueAnimator.ofFloat(view.getTranslationY(),
                view.getTranslationY() + flag * translateY);
        translateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = index; i < itemViews.size(); i++) {
                    itemViews.get(i).setTranslationY((float) animation.getAnimatedValue());
                }
            }
        });

        ValueAnimator alphaAnim;
        if (expand) {
            alphaAnim = ValueAnimator.ofFloat(0f, 1f);
        } else {
            alphaAnim = ValueAnimator.ofFloat(1f, 0f);
        }
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = index; i < itemViews.size(); i++) {
                    itemViews.get(i).setAlpha((float) animation.getAnimatedValue());
                }
            }
        });

        ValueAnimator rotateAnim;
        if (expand) {
            rotateAnim = ValueAnimator.ofInt(90, 0);
        } else {
            rotateAnim = ValueAnimator.ofInt(0, 90);
        }
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = index; i < itemViews.size(); i++) {
                    itemViews.get(i).setRotation((int) animation.getAnimatedValue());
                }
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateAnim, alphaAnim, rotateAnim);
        set.setDuration(200);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animIsPlaying = true;
                if (expand) {
                    for (int i = index; i < itemViews.size(); i++) {
                        itemViews.get(i).setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int curIndex = index;
                if (expand) {
                    if (++curIndex < itemViews.size()) {
                        menuItemAnim(curIndex, true);
                    } else {
                        openMenu = true;
                        animIsPlaying = false;
                    }
                } else {
                    if (--curIndex >= 0) {
                        menuItemAnim(curIndex, false);
                    } else {
                        openMenu = false;
                        animIsPlaying = false;
                    }
                    for (int i = index; i < itemViews.size(); i++) {
                        itemViews.get(i).setVisibility(GONE);
                    }
                }
            }
        });
        set.start();
    }

    public void showActionView(boolean show) {
        if (menuView != null) {
            menuView.setVisibility(show ? VISIBLE : GONE);
            for (View itemView : itemViews) {
                itemView.setVisibility(show ? VISIBLE : GONE);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class DragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == menuView && !animIsPlaying;
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
            for (View itemView : itemViews) {
                itemView.setTranslationX(itemView.getTranslationX() + dx);
                itemView.setTranslationY(itemView.getTranslationY() + dy);
            }
            if (openMenu) {
                View lastItem = itemViews.get(itemViews.size() - 1);
                if (directionUp) {
                    float border = lastItem.getTop() + lastItem.getTranslationY();
                    if (border < contentView.getTop()) {
                        float offset = 0;
                        for (View itemView : itemViews) {
                            offset += itemView.getHeight() + space;
                            itemView.setTranslationY(itemView.getTranslationY() + offset * 2);
                        }
                        directionUp = false;
                    }
                } else {
                    float border = lastItem.getBottom() + lastItem.getTranslationY();
                    if (border > contentView.getBottom()) {
                        float offset = 0;
                        for (View itemView : itemViews) {
                            offset += itemView.getHeight() + space;
                            itemView.setTranslationY(itemView.getTranslationY() - offset * 2);
                        }
                        directionUp = true;
                    }
                }
            } else {
                float totalItemHeight = 0;
                for (View itemView : itemViews) {
                    totalItemHeight += space + itemView.getHeight();
                }
                if (directionUp) {
                    if (top - contentView.getTop() < totalItemHeight) {
                        directionUp = false;
                    }
                } else {
                    if (contentView.getBottom() - top - menuView.getHeight() < totalItemHeight) {
                        directionUp = true;
                    }
                }
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild == menuView) {
                int finalLeft = margin;
                if (releasedChild.getLeft() + releasedChild.getWidth() / 2 > getWidth() / 2) {
                    finalLeft = getWidth() - releasedChild.getWidth() - margin;
                }

                finalPoint.set(finalLeft, releasedChild.getTop());

                if (dragHelper.settleCapturedViewAt(finalPoint.x, finalPoint.y)) {
                    ViewCompat.postInvalidateOnAnimation(FloatMenuLayout.this);
                }
            }
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuClick(View menuItem, int menuIndex);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }
}