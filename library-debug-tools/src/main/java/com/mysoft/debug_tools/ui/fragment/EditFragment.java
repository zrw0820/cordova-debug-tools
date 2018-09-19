package com.mysoft.debug_tools.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.utils.Utils;

/**
 * Created by Zourw on 2018/9/15.
 */
public class EditFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("Content");

        if (getArguments() == null) {
            showError(null);
            return;
        }

        TextView text = view.findViewById(R.id.text);
        final String data = getArguments().getString(PARAM1);
        text.setText(data);

        getToolbar().getMenu().findItem(R.id.menu_copy).setVisible(true);
        getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.copyText(activity, "TableItem", data);
                return true;
            }
        });
    }
}
