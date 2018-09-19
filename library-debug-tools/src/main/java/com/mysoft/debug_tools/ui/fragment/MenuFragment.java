package com.mysoft.debug_tools.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.ui.adapter.MenuAdapter;
import com.mysoft.debug_tools.utils.ViewHelper;
import com.mysoft.debug_tools.utils.permission.AppPermission;
import com.mysoft.debug_tools.utils.permission.PermissionWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zourw on 2018/9/18.
 */
public class MenuFragment extends BaseFragment implements PermissionWatcher.OnGranted {
    private MenuAdapter mMenuAdapter;
    private AppPermission mAppPermission;

    private Pair<Integer, String> checkedItem;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle_view_linear_vertical;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle("云创AppCloud调试工具");

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        ViewHelper.setDefaultDivider(recyclerView);
        mMenuAdapter = new MenuAdapter();
        recyclerView.setAdapter(mMenuAdapter);

        initMenu();

        mAppPermission = new AppPermission.Builder()
                .setActivity(activity)
                .setOnGranted(this)
                .build();
    }

    private void initMenu() {
        List<Pair<Integer, String>> items = new ArrayList<>();
        items.add(Pair.create(R.drawable.icon_sandbox, "沙盒文件浏览"));
        items.add(Pair.create(R.drawable.icon_appinfo, "App信息"));
        items.add(Pair.create(R.drawable.icon_pluginlist, "安装插件列表"));
        items.add(Pair.create(R.drawable.icon_crash, "崩溃日志"));
        mMenuAdapter.setNewData(items);

        mMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Pair<Integer, String> item = (Pair<Integer, String>) adapter.getItem(position);
                if (item == null) {
                    return;
                }
                checkedItem = item;
                switch (position) {
                    case 0:
                        mAppPermission.requestPermission(0x01, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        break;
                    case 1:
                        launch(DeviceInfoFragment.class, checkedItem.second);
                        break;
                    case 2:
                        launch(PluginInfoFragment.class, checkedItem.second);
                        break;
                    case 3:
                        launch(CrashLogFragment.class, checkedItem.second);
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAppPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void granted(int requestCode) {
        switch (requestCode) {
            case 0x01:
                launch(SandboxFragment.class, checkedItem.second);
                break;
        }
    }
}