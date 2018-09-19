package com.mysoft.debug_tools.ui.adapter;

import android.text.TextUtils;
import android.util.Pair;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.entity.SectionEntity;

/**
 * Created by Zourw on 2018/9/15.
 */
public class DeviceInfoAdapter extends BaseSectionQuickAdapter<SectionEntity<Pair<String, String>>, BaseViewHolder> {
    public DeviceInfoAdapter() {
        super(R.layout.item_device_info, R.layout.item_device_info_head, null);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionEntity<Pair<String, String>> item) {
        helper.setText(R.id.head, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, SectionEntity<Pair<String, String>> item) {
        Pair<String, String> data = item.t;
        helper.setVisible(R.id.name, !TextUtils.isEmpty(data.first));
        helper.setText(R.id.name, TextUtils.isEmpty(data.first) ? "" : data.first);
        helper.setText(R.id.content, data.second);
    }
}