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

import com.evrencoskun.tableview.TableView;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.database.DatabaseResult;
import com.mysoft.debug_tools.database.Databases;
import com.mysoft.debug_tools.ui.adapter.TableAdapter;
import com.mysoft.debug_tools.ui.connector.TableViewListener;
import com.mysoft.debug_tools.utils.FileIntent;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zourw on 2018/9/14.
 */
public class TableFragment extends BaseFragment {
    private TableAdapter tableAdapter;

    private File tableFile;
    private String tableName;

    private Databases databases;

    private AsyncTask mTask;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_table;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            tableFile = (File) getArguments().getSerializable(PARAM1);
            tableName = getArguments().getString(PARAM2);
            if (!TextUtils.isEmpty(tableName)) {
                getToolbar().setTitle(tableName);
                getToolbar().getMenu().findItem(R.id.menu_share).setVisible(true);
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FileIntent.share(activity, tableFile);
                        return true;
                    }
                });

                TableView tableView = view.findViewById(R.id.table_view);
                tableAdapter = new TableAdapter(activity);
                tableView.setAdapter(tableAdapter);

                tableView.setTableViewListener(new TableViewListener() {
                    @Override
                    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                        super.onCellClicked(cellView, column, row);
                        String item = tableAdapter.getCellItem(column, row);
                        Bundle bundle = new Bundle();
                        bundle.putString(PARAM1, item);
                        launch(EditFragment.class, bundle);
                    }

                    @Override
                    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
                        String item = tableAdapter.getCellItem(column, row);
                        Utils.copyText(activity, "TableItem", item);
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
        loadData();
    }

    public void loadData() {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, DatabaseResult>() {
            private List<String> columnHeaders;
            private List<String> rowHeaders;
            private List<List<String>> cells;

            @Override
            public void onPreExecute() {
                showLoading();
                columnHeaders = new ArrayList<>();
                rowHeaders = new ArrayList<>();
                cells = new ArrayList<>();
            }

            @Override
            public DatabaseResult doInBackground(Void[] params) {
                if (databases == null) {
                    databases = new Databases(tableFile);
                }
                DatabaseResult result = databases.query(tableName, null);

                if (result.sqlError == null) {
                    columnHeaders.addAll(result.columnNames);
                    List<List<String>> values = result.values;
                    if (values != null && !values.isEmpty()) {
                        for (int i = 0; i < values.size(); i++) {
                            rowHeaders.add(String.valueOf(i));
                            cells.addAll(Collections.singleton(values.get(i)));
                        }
                    }
                }
                return result;
            }

            @Override
            public void onPostExecute(DatabaseResult result) {
                if (result.sqlError == null) {
                    tableAdapter.setAllItems(columnHeaders, rowHeaders, cells);
                } else {
                    showError(null);
                }
                hideLoading();
            }
        }).execute();
    }
}