package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.database.Databases;
import com.mysoft.debug_tools.ui.adapter.NameAdapter;
import com.mysoft.debug_tools.utils.FileIntent;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.ViewHelper;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zourw on 2018/9/14.
 */
public class DBFragment extends BaseFragment {
    private NameAdapter mNameAdapter;
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
                getToolbar().getMenu().findItem(R.id.menu_share).setVisible(true);
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FileIntent.share(activity, file);
                        return true;
                    }
                });

                RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                ViewHelper.setDefaultDivider(recyclerView);
                mNameAdapter = new NameAdapter();
                recyclerView.setAdapter(mNameAdapter);

                mNameAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        String tableName = (String) adapter.getItem(position);
                        if (!TextUtils.isEmpty(tableName)) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PARAM1, file);
                            bundle.putString(PARAM2, tableName);
                            launch(TableFragment.class, bundle);
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
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, List<String>>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public List<String> doInBackground(Void[] params) {
                return new Databases(file).getTableNames();
            }

            @Override
            public void onPostExecute(List<String> result) {
                if (result.isEmpty()) {
                    showError(null);
                } else {
                    Collections.sort(result);
                    mNameAdapter.setNewData(result);
                }
                hideLoading();
            }
        }).execute();
    }
}
