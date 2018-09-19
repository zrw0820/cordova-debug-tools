package com.mysoft.debug_tools.entity;

import java.io.File;

/**
 * Created by Zourw on 2018/9/14.
 */
public class FileInfo {
    private File file;

    public FileInfo() {
    }

    public FileInfo(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}