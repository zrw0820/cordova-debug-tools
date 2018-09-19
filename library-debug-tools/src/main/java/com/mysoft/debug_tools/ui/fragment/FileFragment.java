package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.entity.FileInfo;
import com.mysoft.debug_tools.entity.SectionEntity;
import com.mysoft.debug_tools.ui.adapter.FileAdapter;
import com.mysoft.debug_tools.utils.FileIntent;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;
import com.mysoft.debug_tools.utils.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class FileFragment extends BaseFragment {
    private FileAdapter mFileAdapter;

    private AsyncTask mTask;

    private File file;

    @Override
    protected int getLayoutId() {
        return R.layout.recycle_view_linear_vertical;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            file = (File) getArguments().getSerializable(PARAM1);
            if (file != null) {
                getToolbar().setTitle(file.getName());

                RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                ViewHelper.setDefaultDivider(recyclerView);
                mFileAdapter = new FileAdapter();
                recyclerView.setAdapter(mFileAdapter);

                mFileAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        SectionEntity<FileInfo> fileInfo = (SectionEntity<FileInfo>) adapter.getItem(position);
                        if (fileInfo != null && !fileInfo.isHeader) {
                            File file = fileInfo.t.getFile();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PARAM1, file);
                            if (file.isDirectory()) {
                                launch(FileFragment.class, bundle);
                            } else {
                                if (file.getName().endsWith(".db")) {// 数据库
                                    launch(DBFragment.class, bundle);
                                } else if (FileIntent.isTextFile(file)) {
                                    launch(TextPreviewFragment.class, bundle);
                                } else {
                                    FileIntent.open(activity, file);
                                }
                            }
                        }
                    }
                });
                return;
            }
        }
        showError(null);
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
        if (file != null) {
            loadData(file);
        }
    }

    private void loadData(final File file) {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, List<SectionEntity<FileInfo>>>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public List<SectionEntity<FileInfo>> doInBackground(Void[] params) {
                List<SectionEntity<FileInfo>> entities = new ArrayList<>();
                List<File> files = Utils.getFiles(file);
                if (!files.isEmpty()) {
                    entities.add(new SectionEntity<FileInfo>(true, String.format(Locale.getDefault(), "%d FILES", files.size())));
                    Collections.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                        }
                    });
                }
                for (File file : files) {
                    entities.add(new SectionEntity<>(new FileInfo(file)));
                }
                return entities;
            }

            @Override
            public void onPostExecute(List<SectionEntity<FileInfo>> result) {
                mFileAdapter.setNewData(result);
                hideLoading();
                if (result.isEmpty()) {
                    showError(null);
                }
            }
        }).execute();
    }
}
