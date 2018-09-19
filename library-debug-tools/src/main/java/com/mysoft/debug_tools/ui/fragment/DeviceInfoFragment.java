package com.mysoft.debug_tools.ui.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.entity.SectionEntity;
import com.mysoft.debug_tools.ui.adapter.DeviceInfoAdapter;
import com.mysoft.debug_tools.utils.DimenConvert;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class DeviceInfoFragment extends BaseFragment {
    private DeviceInfoAdapter mInfoAdapter;
    private AsyncTask mTask;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle_view_linear_vertical;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mInfoAdapter = new DeviceInfoAdapter();
        recyclerView.setAdapter(mInfoAdapter);

        mInfoAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                SectionEntity<Pair<String, String>> entity = (SectionEntity<Pair<String, String>>) adapter.getItem(position);
                if (entity != null) {
                    Pair<String, String> data = entity.t;
                    if (data != null) {
                        Utils.copyText(activity, data.first, data.second);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    protected void onViewEnterAnimEnd(View container) {
        super.onViewEnterAnimEnd(container);
        loadData();
    }

    private void loadData() {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, List<SectionEntity<Pair<String, String>>>>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public List<SectionEntity<Pair<String, String>>> doInBackground(Void[] params) {
                List<SectionEntity<Pair<String, String>>> entities = new ArrayList<>();

                PackageManager pm = activity.getPackageManager();
                ApplicationInfo appInfo = activity.getApplicationInfo();

                entities.add(new SectionEntity<Pair<String, String>>(true, "APP信息"));
                entities.add(new SectionEntity<>(Pair.create("AppName：", pm.getApplicationLabel(appInfo).toString())));
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(activity.getPackageName(), 0);
                    entities.add(new SectionEntity<>(Pair.create("ApplicationId：", packageInfo.packageName)));
                    entities.add(new SectionEntity<>(Pair.create("VersionName：", packageInfo.versionName)));
                    entities.add(new SectionEntity<>(Pair.create("VersionCode：", String.valueOf(packageInfo.versionCode))));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    entities.add(new SectionEntity<>(Pair.create("MinSdkVersion：", String.valueOf(appInfo.minSdkVersion))));
                }
                entities.add(new SectionEntity<>(Pair.create("TargetSdkVersion：", String.valueOf(appInfo.targetSdkVersion))));

                try {
                    PackageInfo packageInfo = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        Signature signature = packageInfo.signatures[0];
                        if (signature != null) {
                            List<Pair<String, String>> signatures = Utils.parser(signature);
                            if (!signatures.isEmpty()) {
                                entities.add(new SectionEntity<Pair<String, String>>(true, "APP签名"));
                                for (Pair<String, String> pair : signatures) {
                                    entities.add(new SectionEntity<>(pair));
                                }
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    PackageInfo packageInfo = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
                    entities.add(new SectionEntity<Pair<String, String>>(true, "APP权限"));
                    for (String permission : packageInfo.requestedPermissions) {
                        entities.add(new SectionEntity<>(Pair.create("", permission)));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                entities.add(new SectionEntity<Pair<String, String>>(true, "设备信息"));
                entities.add(new SectionEntity<>(Pair.create("型号：", Build.MODEL)));
                entities.add(new SectionEntity<>(Pair.create("品牌：", Build.BRAND)));
                entities.add(new SectionEntity<>(Pair.create("Android版本：", Build.VERSION.RELEASE)));
                entities.add(new SectionEntity<>(Pair.create("CPU：", Build.HARDWARE)));
                entities.add(new SectionEntity<>(Pair.create("分辨率：", String.format(Locale.getDefault(), "%d*%d",
                        DimenConvert.getScreenWidth(), DimenConvert.getScreenHeight()))));

                return entities;
            }

            @Override
            public void onPostExecute(List<SectionEntity<Pair<String, String>>> result) {
                mInfoAdapter.setNewData(result);
                hideLoading();
            }
        }).execute();
    }
}
