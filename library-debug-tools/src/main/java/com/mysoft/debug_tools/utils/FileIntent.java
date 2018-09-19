package com.mysoft.debug_tools.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by Zourw on 2018/9/15.
 */
public class FileIntent {
    private static final HashMap<String, String> mFileTypes = new HashMap<>();

    static {
        mFileTypes.put("apk", "application/vnd.android.package-archive");
        mFileTypes.put("avi", "video/x-msvideo");
        mFileTypes.put("bmp", "image/bmp");
        mFileTypes.put("c", "text/plain");
        mFileTypes.put("class", "application/octet-stream");
        mFileTypes.put("conf", "text/plain");
        mFileTypes.put("doc", "application/msword");
        mFileTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mFileTypes.put("xls", "application/vnd.ms-excel");
        mFileTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mFileTypes.put("gif", "image/gif");
        mFileTypes.put("gtar", "application/x-gtar");
        mFileTypes.put("gz", "application/x-gzip");
        mFileTypes.put("htm", "text/html");
        mFileTypes.put("html", "text/html");
        mFileTypes.put("jar", "application/java-archive");
        mFileTypes.put("java", "text/plain");
        mFileTypes.put("jpeg", "image/jpeg");
        mFileTypes.put("jpg", "image/jpeg");
        mFileTypes.put("js", "application/x-javascript");
        mFileTypes.put("log", "text/plain");
        mFileTypes.put("mov", "video/quicktime");
        mFileTypes.put("mp3", "audio/x-mpeg");
        mFileTypes.put("mp4", "video/mp4");
        mFileTypes.put("mpeg", "video/mpeg");
        mFileTypes.put("mpg", "video/mpeg");
        mFileTypes.put("mpg4", "video/mp4");
        mFileTypes.put("ogg", "audio/ogg");
        mFileTypes.put("pdf", "application/pdf");
        mFileTypes.put("png", "image/png");
        mFileTypes.put("ppt", "application/vnd.ms-powerpoint");
        mFileTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mFileTypes.put("prop", "text/plain");
        mFileTypes.put("rc", "text/plain");
        mFileTypes.put("rmvb", "audio/x-pn-realaudio");
        mFileTypes.put("rtf", "application/rtf");
        mFileTypes.put("sh", "text/plain");
        mFileTypes.put("tar", "application/x-tar");
        mFileTypes.put("tgz", "application/x-compressed");
        mFileTypes.put("txt", "text/plain");
        mFileTypes.put("wav", "audio/x-wav");
        mFileTypes.put("wps", "application/vnd.ms-works");
        mFileTypes.put("xml", "text/plain");
        mFileTypes.put("zip", "application/x-zip-compressed");
        mFileTypes.put("", "*/*");
    }

    public static String getFileType(File file) {
        if (!file.exists()) {
            return "";
        }
        String name = file.getName();
        try {
            String suffix = !name.contains(".") ? "" : name.substring(name.lastIndexOf(".") + 1);
            String type = mFileTypes.get(suffix);
            Timber.d("getFileType: %s", type);
            return type;
        } catch (Throwable t) {
            t.printStackTrace();
            return "";
        }
    }

    public static boolean isTextFile(File file) {
        return "text/plain".equals(getFileType(file));
    }

    public static void open(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
            intent.setDataAndType(uri, getFileType(file));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void share(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
            share.setDataAndType(uri, getFileType(file));
            context.startActivity(Intent.createChooser(share, "分享文件"));
        }
    }
}
