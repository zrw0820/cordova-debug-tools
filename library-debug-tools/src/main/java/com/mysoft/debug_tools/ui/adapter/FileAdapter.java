package com.mysoft.debug_tools.ui.adapter;

import android.text.format.Formatter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.entity.FileInfo;
import com.mysoft.debug_tools.entity.SectionEntity;
import com.mysoft.debug_tools.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 * Created by Zourw on 2018/9/14.
 */
public class FileAdapter extends BaseSectionQuickAdapter<SectionEntity<FileInfo>, BaseViewHolder> {
    public FileAdapter() {
        super(R.layout.item_file, R.layout.item_file_head, null);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionEntity<FileInfo> item) {
        helper.setText(R.id.item_head, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, SectionEntity<FileInfo> item) {
        File file = item.t.getFile();
        helper.setText(R.id.item_title, file.getName());
        if (file.isDirectory()) {
            helper.setText(R.id.item_info, String.format(Locale.getDefault(), "%d items    %s",
                    Utils.getCount(file.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return !name.startsWith(".");
                        }
                    })), Utils.millis2String(file.lastModified(), Utils.WITHOUT_MILLIS)));
            helper.setVisible(R.id.item_arrow, true);
        } else {
            helper.setText(R.id.item_info, String.format(Locale.getDefault(), "%s    %s",
                    Formatter.formatFileSize(helper.itemView.getContext(), file.length()),
                    Utils.millis2String(file.lastModified(), Utils.WITHOUT_MILLIS)));
            helper.setVisible(R.id.item_arrow, false);
        }
    }
}