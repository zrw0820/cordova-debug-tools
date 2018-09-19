package com.mysoft.debug_tools.ui.adapter;

import android.util.Pair;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mysoft.debug_tools.R;

import java.util.Locale;

/**
 * Created by Zourw on 2018/9/15.
 */
public class PluginAdapter extends BaseQuickAdapter<Pair<String, String>, BaseViewHolder> {
    public PluginAdapter() {
        super(R.layout.item_plugin);
    }

    @Override
    protected void convert(BaseViewHolder helper, Pair<String, String> item) {
        helper.setText(R.id.item_title, item.first);
        helper.setText(R.id.item_text, String.format(Locale.getDefault(), "VERSION: %s", item.second));
    }
}