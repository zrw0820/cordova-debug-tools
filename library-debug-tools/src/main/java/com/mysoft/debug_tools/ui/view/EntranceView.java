package com.mysoft.debug_tools.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.ui.activity.CDTActivity;

/**
 * Created by Zourw on 2018/9/12.
 */
public class EntranceView extends AppCompatImageView {
    private float lastX;
    private float lastY;
    private boolean click;

    private float middleX;

    private boolean isOpen;

    public EntranceView(final Context context) {
        super(context);

        middleX = context.getResources().getDisplayMetrics().widthPixels / 2;

        setImageResource(R.drawable.icon_debug);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CDTActivity.start(context);
            }
        });
    }

    public WindowManager windowManager() {
        return ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = event.getRawX();
                lastY = event.getRawY();
                click = true;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                float offX = event.getRawX() - lastX;
                float offY = event.getRawY() - lastY;
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                params.x += offX;
                params.y += offY;
                windowManager().updateViewLayout(this, params);
                lastX = event.getRawX();
                lastY = event.getRawY();
                click = false;
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (click) {
                    performClick();
                } else {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                    float targetX;
                    if (params.x + getWidth() / 2 < middleX) {
                        targetX = 0;
                    } else {
                        targetX = (int) (middleX * 2 - getWidth());
                    }
                    ValueAnimator animator = ValueAnimator.ofFloat(params.x, targetX)
                            .setDuration(200);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                            params.x = (int) value;
                            windowManager().updateViewLayout(EntranceView.this, params);
                        }
                    });
                    animator.start();
                }
            }
            break;
            default:
                break;
        }
        return true;
    }

    public void open() {
        if (isOpen) {
            return;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.START;

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        params.x = 0;
        params.y = metrics.heightPixels / 5 * 4;
        windowManager().addView(this, params);
        isOpen = true;
    }

    public void close() {
        if (!isOpen) {
            return;
        }
        windowManager().removeView(this);
        isOpen = false;
    }
}