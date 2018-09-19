package com.mysoft.debug_tools.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.utils.FileIntent;
import com.mysoft.debug_tools.utils.SimpleTask;
import com.mysoft.debug_tools.utils.Utils;

import java.io.File;

/**
 * Created by Zourw on 2018/9/15.
 */
public class TextPreviewFragment extends BaseFragment {
    private TextView content;
    private AsyncTask mTask;

    private File textFile;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_text_preview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        content = view.findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());

        if (getArguments() != null) {
            textFile = (File) getArguments().getSerializable(PARAM1);
            if (textFile != null) {
                getToolbar().setTitle(textFile.getName());
                getToolbar().getMenu().findItem(R.id.menu_share).setVisible(true);
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FileIntent.share(activity, textFile);
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
        if (textFile != null) {
            loadData(textFile);
        }
    }

    private void loadData(final File file) {
        mTask = new SimpleTask<>(new SimpleTask.Callback<Void, String>() {
            @Override
            public void onPreExecute() {
                showLoading();
            }

            @Override
            public String doInBackground(Void[] params) {
                return Utils.readTextFile(file);
            }

            @Override
            public void onPostExecute(final String result) {
                if (TextUtils.isEmpty(result)) {
                    showError(null);
                } else {
                    content.setText(result);
                    content.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Utils.copyText(activity, "text", result);
                            return true;
                        }
                    });
                }
                hideLoading();
            }
        }).execute();
    }
}
