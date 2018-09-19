package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.ui.adapter.PluginAdapter;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;
import com.mysoft.debug_tools.utils.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class PluginInfoFragment extends BaseFragment {
    private TextView mPluginCount;
    private PluginAdapter mPluginAdapter;

    private AsyncTask mTask;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_plugin_info;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPluginCount = view.findViewById(R.id.plugin_count);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        ViewHelper.setDefaultDivider(recyclerView);
        mPluginAdapter = new PluginAdapter();
        recyclerView.setAdapter(mPluginAdapter);
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
        loadPlugins();
    }

    private void loadPlugins() {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, List<Pair<String, String>>>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public List<Pair<String, String>> doInBackground(Void[] params) {
                List<Pair<String, String>> result = new ArrayList<>();
                File pluginConfig = new File(activity.getFilesDir(), "www" + File.separator + "cordova_plugins.js");
                if (pluginConfig.exists()) {
                    String content = Utils.readTextFile(pluginConfig);
                    int metadataIndex = content.indexOf("module.exports.metadata");
                    if (metadataIndex >= 0) {
                        String temp = content.substring(metadataIndex);
                        int beginIndex = temp.indexOf("{");
                        int endIndex = temp.indexOf("}");
                        String json = temp.substring(beginIndex, endIndex + 1);
                        try {
                            JSONObject plugins = new JSONObject(json);
                            Iterator<String> keys = plugins.keys();
                            while (keys.hasNext()) {
                                String name = keys.next();
                                String version = plugins.getString(name);
                                Pair<String, String> item = new Pair<>(name, version);
                                result.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return result;
            }

            @Override
            public void onPostExecute(List<Pair<String, String>> result) {
                if (result == null || result.isEmpty()) {
                    showError(null);
                } else {
                    mPluginCount.setText(String.format(Locale.getDefault(), "已安装插件：%d个", result.size()));
                    mPluginAdapter.setNewData(result);
                }
                hideLoading();
            }
        }).execute();
    }
}