package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.crash.Report;
import com.mysoft.debug_tools.utils.FileIntent;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Zourw on 2018/9/15.
 */
public class CrashPreviewFragment extends BaseFragment {
    private TextView headerText;
    private TextView stacktraceText;

    private AsyncTask mTask;
    private File logFile;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_crash_preview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headerText = view.findViewById(R.id.header);
        stacktraceText = view.findViewById(R.id.stacktrace);

        stacktraceText.setHorizontallyScrolling(true);
        stacktraceText.setMovementMethod(new ScrollingMovementMethod());

        if (getArguments() != null) {
            logFile = (File) getArguments().getSerializable(PARAM1);
            if (logFile != null) {
                getToolbar().setTitle(logFile.getName());
                getToolbar().getMenu().findItem(R.id.menu_share).setVisible(true);
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FileIntent.share(activity, logFile);
                        return true;
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
        if (logFile != null) {
            loadData(logFile);
        }
    }

    private void loadData(final File file) {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, Report>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public Report doInBackground(Void[] params) {
                String content = Utils.readTextFile(file);
                try {
                    JSONObject log = new JSONObject(content);
                    return Report.jsonToReport(log);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPostExecute(Report report) {
                if (report != null) {
                    headerText.setText(report.header);
                    stacktraceText.setText(report.trace);
                }
                hideLoading();
            }
        }).execute();
    }
}
