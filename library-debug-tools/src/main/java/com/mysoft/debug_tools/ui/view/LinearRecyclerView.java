package com.mysoft.debug_tools.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Zourw on 2018/9/16.
 */
public class LinearRecyclerView extends android.support.v7.widget.RecyclerView {
    public LinearRecyclerView(@NonNull Context context) {
        super(context);
        defaultManager(context);
    }

    public LinearRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        defaultManager(context);
    }

    public LinearRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defaultManager(context);
    }

    private void defaultManager(Context context) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(manager);
    }
}
