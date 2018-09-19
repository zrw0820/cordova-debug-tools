package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.crash.CrashKep;
import com.mysoft.debug_tools.entity.FileInfo;
import com.mysoft.debug_tools.entity.SectionEntity;
import com.mysoft.debug_tools.ui.adapter.FileAdapter;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.ViewHelper;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class CrashLogFragment extends BaseFragment {
    private FileAdapter mFileAdapter;
    private AsyncTask mTask;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle_view_linear_vertical;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        ViewHelper.setDefaultDivider(recyclerView);
        mFileAdapter = new FileAdapter();
        recyclerView.setAdapter(mFileAdapter);

        mFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SectionEntity<FileInfo> fileInfo = (SectionEntity<FileInfo>) adapter.getItem(position);
                if (fileInfo != null && !fileInfo.isHeader) {
                    File log = fileInfo.t.getFile();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PARAM1, log);
                    launch(CrashPreviewFragment.class, bundle);
                }
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
        loadLogList();
    }

    private void loadLogList() {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, List<SectionEntity<FileInfo>>>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public List<SectionEntity<FileInfo>> doInBackground(Void[] params) {
                List<SectionEntity<FileInfo>> entities = new ArrayList<>();

                File logDir = new File(activity.getApplicationInfo().dataDir, CrashKep.LOG_PATH);
                List<File> files = Arrays.asList(logDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.exists() && pathname.isFile();
                    }
                }));
                Collections.reverse(files);
                if (!files.isEmpty()) {
                    entities.add(new SectionEntity<FileInfo>(true, String.format(Locale.getDefault(), "%d FILES", files.size())));
                }
                for (File file : files) {
                    entities.add(new SectionEntity<>(new FileInfo(file)));
                }

                return entities;
            }

            @Override
            public void onPostExecute(List<SectionEntity<FileInfo>> result) {
                if (result == null || result.isEmpty()) {
                    showError(null);
                } else {
                    mFileAdapter.setNewData(result);
                }
                hideLoading();
            }
        }).execute();
    }
}