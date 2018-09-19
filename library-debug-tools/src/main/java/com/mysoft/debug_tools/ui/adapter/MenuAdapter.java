package com.mysoft.debug_tools.ui.adapter;

import android.util.Pair;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mysoft.debug_tools.R;

/**
 * Created by Zourw on 2018/9/14.
 */
public class MenuAdapter extends BaseQuickAdapter<Pair<Integer, String>, BaseViewHolder> {
    public MenuAdapter() {
        super(R.layout.item_menu);
    }

    @Override
    protected void convert(BaseViewHolder helper, Pair<Integer, String> item) {
        helper.setImageResource(R.id.icon, item.first);
        helper.setText(R.id.text, item.second);
    }
}
