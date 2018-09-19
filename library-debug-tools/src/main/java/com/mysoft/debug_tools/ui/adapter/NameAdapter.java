package com.mysoft.debug_tools.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mysoft.debug_tools.R;

/**
 * Created by Zourw on 2018/9/14.
 */
public class NameAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public NameAdapter() {
        super(R.layout.item_name);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.name, item);
    }
}
